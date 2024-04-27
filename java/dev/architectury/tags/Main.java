package dev.architectury.tags;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

	private static List<Tag> tags;

	public static void main(String[] args) throws IOException {
		//archGen();
		String s = Files.readString(Path.of("tags/convention-tags.tags"));
		Main.tags = Tag.deserialize(s);
		HttpServer server = HttpServer.create();
		server.bind(new InetSocketAddress(1290), 0);
		server.start();
		String indexTemplate = new String(Main.class.getResourceAsStream("/index-template.html").readAllBytes());
		String tagTemplate = new String(Main.class.getResourceAsStream("/tag-template.html").readAllBytes());
		String loaderTemplate = new String(Main.class.getResourceAsStream("/loader-template.html").readAllBytes());
		String architecturyTemplate = new String(Main.class.getResourceAsStream("/arch-template.html").readAllBytes());
		String script = new String(Main.class.getResourceAsStream("/index.js").readAllBytes());
		server.createContext("/index.js", exchange -> {
			String string = script;
			byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
			int length = bytes.length;
			exchange.sendResponseHeaders(200, length);
			exchange.getResponseBody().write(bytes);
			exchange.close();
		});
		server.createContext("/save", exchange -> {
			if (exchange.getRequestMethod().equals("POST")) {
				byte[] bytes = exchange.getRequestBody().readAllBytes();
				Gson gson = new Gson();
				JsonObject object = gson.fromJson(new String(bytes), JsonObject.class);
				for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
					Map.Entry<String, JsonObject> stringJsonElementEnt = (Map.Entry<String, JsonObject>) (Object) stringJsonElementEntry;
					for (Map.Entry<String, JsonElement> jsonElementEntry : stringJsonElementEnt.getValue().entrySet()) {
						Map.Entry<String, JsonObject> d = (Map.Entry<String, JsonObject>) (Object) jsonElementEntry;
						Optional<Tag> first = tags.stream().filter(a -> stringJsonElementEnt.getKey().equals(a.registryKey()) && d.getKey().equals(a.name())).findFirst();
						Tag tag = first.orElseThrow();
						tags.set(tags.indexOf(tag), new Tag(tag.registryKey(), tag.name(), d.getValue().getAsJsonPrimitive("javadoc").getAsString().isBlank() ? new String[0] : d.getValue().getAsJsonPrimitive("javadoc").getAsString().split("\n"), tag.fabric(), tag.neoForge(), new Tag.Method(d.getValue().getAsJsonPrimitive("class").getAsString().replaceAll("\\.", "/"), d.getValue().getAsJsonPrimitive("field").getAsString())));
					}
				}
				Files.writeString(Path.of("tags/convention-tags.tags"), Tag.serialize(tags));
				exchange.sendResponseHeaders(200, 0);
				exchange.close();
			}
		});
		server.createContext("/", exchange -> {
			String string = null;
			try {
				string = indexHtml(tags, indexTemplate, tagTemplate, loaderTemplate, architecturyTemplate);
			} catch (Exception e) {
				e.printStackTrace();
				string = "<h1>Internal Server Error</h1>";
				byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
				int length = bytes.length;
				exchange.sendResponseHeaders(500, length);
				exchange.getResponseBody().write(bytes);
				exchange.close();
				throw e;
			}

			byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
			int length = bytes.length;
			exchange.sendResponseHeaders(200, length);
			exchange.getResponseBody().write(bytes);
			exchange.close();
		});
		System.out.println("Server started at http://localhost:1290");
	}

	private static String indexHtml(List<Tag> deserialize, String indexTemplate, String tagTemplate, String loaderTemplate, String architecturyTemplate) {
		String[] architecturyClasses = new String[]{
				"dev.architectury.tags.BiomeTags",
				"dev.architectury.tags.BlockTags",
				"dev.architectury.tags.EnchantmentTags",
				"dev.architectury.tags.EntityTypeTags",
				"dev.architectury.tags.FluidTags",
				"dev.architectury.tags.ItemTags",
				"dev.architectury.tags.StructureTags",
		};
		String string = indexTemplate;
		String f = "";
		for (Tag tag : deserialize) {
			String g = "";
			Tag.Method fabric = tag.fabric();
			if (fabric == null) fabric = new Tag.Method("", "");
			g += loaderTemplate.formatted("Fabric", prettyClass(fabric.clazz()), fabric.method());
			Tag.Method neoforge = tag.neoForge();
			if (neoforge == null) neoforge = new Tag.Method("", "");
			g += loaderTemplate.formatted("NeoForge", prettyClass(neoforge.clazz()), neoforge.method());
			Tag.Method architectury = tag.architectury();
			if (architectury == null) architectury = new Tag.Method("", "");
			String archClass = architectury.clazz();
			String archMethod = architectury.method();
			String selectOptions = "";
			boolean r = false;
			for (String architecturyClass : architecturyClasses) {
				String pretty = prettyClass(architecturyClass);
				if (archClass.replaceAll("/", ".").equals(architecturyClass)) {
					selectOptions += "<option selected value=\"" + architecturyClass + "\">" + pretty + "</option>";
					r = true;
				} else {
					selectOptions += "<option value=\"" + architecturyClass + "\">" + pretty + "</option>";
				}
			}
			if (!r) {
				selectOptions += "<option disabled hidden selected></option>";
			}
			g += architecturyTemplate.formatted(selectOptions, archMethod);
			f += tagTemplate.formatted(
					(tag.registryKey() + "-" + tag.name()).replaceAll("/", "-").replaceAll(":", "-"),
					tag.name(), tag.registryKey(),
					tag.registryKey() + ": " + tag.name(),
					g,
					String.join("\n", tag.comments())
			);
		}
		string = string.formatted(f);
		return string;
	}

	private static String prettyClass(String clazz) {
		String s = clazz.replaceAll("/", ".").replaceAll("\\$", ".");
		String[] split = s.split("\\.");
		return split[split.length - 1];
	}

	public static void archGen() throws IOException {
		HashMap<String, Tag.TagBuilder> tagHashMap = new HashMap<>();
		String s = Files.readString(Path.of("archgen/fabric.txt"));
		extract(tagHashMap, s, "fabric");
		s = Files.readString(Path.of("archgen/neoforge.txt"));
		extract(tagHashMap, s, "neoforge");

		Collection<Tag.TagBuilder> values = tagHashMap.values();
		List<Tag> tags = values.stream().map(Tag.TagBuilder::build).toList();
		Files.writeString(Path.of("tags/convention-tags.tags"), Tag.serialize(tags));
	}

	private static void extract(HashMap<String, Tag.TagBuilder> tagHashMap, String s, String loader) {
		String[] split = s.split("\n");
		for (String s1 : split) {
			String[] split1 = s1.split("\\|");
			String type = split1[0];
			String clazz = split1[1];
			String method = split1[2];
			String tag = split1[3];
			tagHashMap.computeIfAbsent(type + "/" + tag, f -> new Tag.TagBuilder());
			Tag.TagBuilder tagBuilder = tagHashMap.get(type + "/" + tag);
			tagBuilder.name(type, tag);
			switch (loader) {
				case "fabric" -> tagBuilder.fabric(clazz.replaceAll("\\.", "/"), method);
				case "neoforge" -> tagBuilder.neoforge(clazz.replaceAll("\\.", "/"), method);
			}
		}
	}
}
