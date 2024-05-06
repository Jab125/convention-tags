package dev.jab125.convention.tags;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
		String conventionTemplate = new String(Main.class.getResourceAsStream("/arch-template.html").readAllBytes());
		String script = new String(Main.class.getResourceAsStream("/index.js").readAllBytes());
		String styles = new String(Main.class.getResourceAsStream("/style.css").readAllBytes());
		String manifest = new String(Main.class.getResourceAsStream("/manifest.json").readAllBytes());
		byte[] x512 = Main.class.getResourceAsStream("/512x.png").readAllBytes();
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
		System.out.println("Server started at http://localhost:1290");
	}

	private static String indexHtml(List<Tag> deserialize, String indexTemplate, String tagTemplate, String loaderTemplate, String conventionTemplate) {
		String[] conventionClasses = new String[]{
				"dev.jab125.tags.BiomeTags",
				"dev.jab125.tags.BlockTags",
				"dev.jab125.tags.EnchantmentTags",
				"dev.jab125.tags.EntityTypeTags",
				"dev.jab125.tags.FluidTags",
				"dev.jab125.tags.ItemTags",
				"dev.jab125.tags.StructureTags",
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
			Tag.Method convention = tag.convention();
			if (convention == null) convention = new Tag.Method("", "");
			String archClass = convention.clazz();
			String archMethod = convention.method();
			String selectOptions = "";
			boolean r = false;
			for (String conventionClass : conventionClasses) {
				String pretty = prettyClass(conventionClass);
				if (archClass.replaceAll("/", ".").equals(conventionClass)) {
					selectOptions += "<option selected value=\"" + conventionClass + "\">" + pretty + "</option>";
					r = true;
				} else {
					selectOptions += "<option value=\"" + conventionClass + "\">" + pretty + "</option>";
				}
			}
			if (!r) {
				selectOptions += "<option disabled hidden selected></option>";
			}
			g += conventionTemplate.formatted(selectOptions, archMethod);
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
