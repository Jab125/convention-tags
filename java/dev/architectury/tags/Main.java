package dev.architectury.tags;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		//archGen();
		String s = Files.readString(Path.of("tags/convention-tags.tags"));
		List<Tag> deserialize = Tag.deserialize(s);
		HttpServer server = HttpServer.create();
		server.bind(new InetSocketAddress(1290), 0);
		server.start();
		String indexTemplate = new String(Main.class.getResourceAsStream("/index-template.html").readAllBytes());
		String tagTemplate = new String(Main.class.getResourceAsStream("/tag-template.html").readAllBytes());
		String loaderTemplate = new String(Main.class.getResourceAsStream("/loader-template.html").readAllBytes());
		String architecturyTemplate = new String(Main.class.getResourceAsStream("/arch-template.html").readAllBytes());
		server.createContext("/", exchange -> {
			String string = null;
			try {
				string = indexHtml(deserialize, indexTemplate, tagTemplate, loaderTemplate, architecturyTemplate);
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
			g += architecturyTemplate.formatted(prettyClass(archClass), archMethod);
			f += tagTemplate.formatted(
					(tag.registryKey() + "-" + tag.name()).replaceAll("/", "-").replaceAll(":", "-"),
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
