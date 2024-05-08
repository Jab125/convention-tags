package dev.jab125.convention.tags;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import dev.jab125.convention.tags.util.TagMerger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TagsGui {

	private static List<Tag> tags;

	public static void main(String[] args) throws IOException {
		//cGen();
		String s = Files.readString(Path.of("tags/convention-tags.tags"));
		TagsGui.tags = Tag.deserialize(s);
		//TagGeneration.main(args);tags = TagMerger.merge(TagGeneration.mutableTags.stream().map(a -> a.build()).toList(), tags);
		HttpServer server = HttpServer.create();
		server.bind(new InetSocketAddress(1291), 0);
		server.start();
		String indexTemplate = new String(TagsGui.class.getResourceAsStream("/index-template.html").readAllBytes());
		String tagTemplate = new String(TagsGui.class.getResourceAsStream("/tag-template.html").readAllBytes());
		String loaderTemplate = new String(TagsGui.class.getResourceAsStream("/loader-template.html").readAllBytes());
		String conventionTemplate = new String(TagsGui.class.getResourceAsStream("/convention-template.html").readAllBytes());
		String script = new String(TagsGui.class.getResourceAsStream("/index.js").readAllBytes());
		String styles = new String(TagsGui.class.getResourceAsStream("/style.css").readAllBytes());
		String manifest = new String(TagsGui.class.getResourceAsStream("/manifest.json").readAllBytes());
		byte[] x512 = TagsGui.class.getResourceAsStream("/512x.png").readAllBytes();
		server.createContext("/icons/512.png", exchange -> {
			byte[] bytes = x512;
			int length = bytes.length;
			exchange.sendResponseHeaders(200, length);
			exchange.getResponseBody().write(bytes);
			exchange.close();
		});
		server.createContext("/manifest.json", exchange -> {
			String string = manifest;
			byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
			int length = bytes.length;
			exchange.sendResponseHeaders(200, length);
			exchange.getResponseBody().write(bytes);
			exchange.close();
		});
		server.createContext("/index.js", exchange -> {
			String string = script;
			byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
			int length = bytes.length;
			exchange.sendResponseHeaders(200, length);
			exchange.getResponseBody().write(bytes);
			exchange.close();
		});
		server.createContext("/style.css", exchange -> {
			String string = styles;
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
						HashMap<Ecosystem, Tag.Field> newMap = new HashMap<>(tag.fields());
						newMap.put(Ecosystem.COMMON, new Tag.Field(d.getValue().getAsJsonPrimitive("class").getAsString().replaceAll("\\.", "/"), d.getValue().getAsJsonPrimitive("field").getAsString()));
						tags.set(tags.indexOf(tag), new Tag(tag.registryKey(), tag.name(), d.getValue().getAsJsonPrimitive("javadoc").getAsString().isBlank() ? new String[0] : d.getValue().getAsJsonPrimitive("javadoc").getAsString().split("\n"), newMap));
					}
				}
				Files.writeString(Path.of("tags/convention-tags.tags"), Tag.serialize(tags));
				Files.writeString(Path.of("tags/output.tags"), Tag.serializeMini(tags));
				exchange.sendResponseHeaders(200, 0);
				exchange.close();
			}
		});
		server.createContext("/", exchange -> {
			String string = null;
			try {
				string = indexHtml(tags, indexTemplate, tagTemplate, loaderTemplate, conventionTemplate);
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
		System.out.println("Server started at http://localhost:1291");
	}

	private static String indexHtml(List<Tag> deserialize, String indexTemplate, String tagTemplate, String loaderTemplate, String conventionTemplate) {
		String[] conventionClasses = new String[]{
				"dev.jab125.tags.ConventionalBiomeTags",
				"dev.jab125.tags.ConventionalBlockTags",
				"dev.jab125.tags.ConventionalEnchantmentTags",
				"dev.jab125.tags.ConventionalEntityTypeTags",
				"dev.jab125.tags.ConventionalFluidTags",
				"dev.jab125.tags.ConventionalItemTags",
				"dev.jab125.tags.ConventionalStructureTags",
		};
		String string = indexTemplate;
		String f = "";
		for (Tag tag : deserialize) {
			String g = "";
			for (Ecosystem ecosystem : Ecosystem.ECOSYSTEMS_NO_COMMON) {
				Tag.Field field = tag.fields().get(ecosystem);
				if (field == null) field = new Tag.Field("", "");
				g += loaderTemplate.formatted(ecosystem.name(), prettyClass(field.clazz()), field.method());
			}
			Tag.Field convention = tag.fields().get(Ecosystem.COMMON);
			if (convention == null) convention = new Tag.Field("", "");
			String cClass = convention.clazz();
			String cMethod = convention.method();
			String selectOptions = "";
			boolean r = false;
			for (String conventionClass : conventionClasses) {
				String pretty = prettyClass(conventionClass);
				if (cClass.replaceAll("/", ".").equals(conventionClass)) {
					selectOptions += "<option selected value=\"" + conventionClass + "\">" + pretty + "</option>";
					r = true;
				} else {
					selectOptions += "<option value=\"" + conventionClass + "\">" + pretty + "</option>";
				}
			}
			if (!r) {
				selectOptions += "<option disabled hidden selected></option>";
			}
			g += conventionTemplate.formatted(selectOptions, cMethod);
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

	public static void generate() throws IOException {

	}
}
