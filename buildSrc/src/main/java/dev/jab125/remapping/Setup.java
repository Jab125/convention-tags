package dev.jab125.remapping;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.format.proguard.ProGuardFileReader;
import net.fabricmc.mappingio.format.tiny.Tiny2FileReader;
import net.fabricmc.mappingio.format.tiny.Tiny2FileWriter;
import net.fabricmc.mappingio.tree.MemoryMappingTree;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.ZipFile;

public class Setup {
	public static final String MINECRAFT_VERSION = "1.20.5";
	public static final String INTERMEDIARY_URL = "https://maven.fabricmc.net/net/fabricmc/intermediary/%s/intermediary-%s-v2.jar".formatted(MINECRAFT_VERSION, MINECRAFT_VERSION);
	public static final Path REMAP_DIR = Path.of("remap");
	public static final Path INTERMEDIARY_FILE = REMAP_DIR.resolve("intermediary.jar");
	public static final Path EXTRACTED_INTERMEDIARY = REMAP_DIR.resolve("intermediary.tiny");
	public static final Path CLIENT_MAPPINGS = REMAP_DIR.resolve("client.txt");
	public static final Path SERVER_MAPPINGS = REMAP_DIR.resolve("server.txt");
	public static final Path JOINED = REMAP_DIR.resolve("joined.tiny");
	public static final String PISTON_META = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
	private static boolean setup;

	public static void setup() throws IOException {
		if (JOINED.toFile().exists()) return;
		try(Stopwatch create = new Stopwatch("Create Directories")) {
			REMAP_DIR.toFile().mkdirs();
		}
		try(Stopwatch download = new Stopwatch("Download")) {
			downloadIntermediary();
			downloadMojangMappings();
		}
		try(Stopwatch extract = new Stopwatch("Extract Intermediary")) {
			extractIntermediary();
		}
		try(Stopwatch join = new Stopwatch("Join")) {
			joinMappings();
		}
	}

	private static void extractIntermediary() throws IOException {
		try (ZipFile zf = new ZipFile(INTERMEDIARY_FILE.toFile())) {
			Files.write(EXTRACTED_INTERMEDIARY, zf.getInputStream(zf.getEntry("mappings/mappings.tiny")).readAllBytes());
		}
	}

	private static void joinMappings() throws IOException {
		MemoryMappingTree tree = new MemoryMappingTree();
		ProGuardFileReader.read(new FileReader(CLIENT_MAPPINGS.toFile()), "mojang", "official", tree);
		ProGuardFileReader.read(new FileReader(SERVER_MAPPINGS.toFile()), "mojang", "official", tree);
		Tiny2FileReader.read(new FileReader(EXTRACTED_INTERMEDIARY.toFile()), tree);
		Tiny2FileWriter tiny2FileWriter = new Tiny2FileWriter(new FileWriter(JOINED.toFile()), false);
		MappingSourceNsSwitch official = new MappingSourceNsSwitch(tiny2FileWriter, "official");
		tree.accept(official);
	}

	private static void downloadIntermediary() throws IOException {
		byte[] bytes = getBytes(new URL(INTERMEDIARY_URL));
		Files.write(INTERMEDIARY_FILE, bytes);
	}

	private static void downloadMojangMappings() throws IOException{
		String pistonMetaString = new String(getBytes(new URL(PISTON_META)));
		JsonObject object = new Gson().fromJson(pistonMetaString, JsonObject.class);
		Optional<JsonElement> version = object.getAsJsonArray("versions").asList().stream().filter(a -> a.getAsJsonObject().get("id").getAsJsonPrimitive().getAsString().equals(MINECRAFT_VERSION)).findFirst();
		JsonObject obj = version.orElseThrow().getAsJsonObject();
		String newUrl = obj.getAsJsonPrimitive("url").getAsString();
		String newUrlMeta = new String(getBytes(new URL(newUrl)));
		JsonObject v = new Gson().fromJson(newUrlMeta, JsonObject.class);
		JsonObject downloads = v.getAsJsonObject("downloads");
		String clientMappingsUrl = downloads.getAsJsonObject("client_mappings").getAsJsonPrimitive("url").getAsString();
		String serverMappingsUrl = downloads.getAsJsonObject("server_mappings").getAsJsonPrimitive("url").getAsString();

		Files.writeString(CLIENT_MAPPINGS, new String(getBytes(new URL(clientMappingsUrl))));
		Files.writeString(SERVER_MAPPINGS, new String(getBytes(new URL(serverMappingsUrl))));
	}

	private static byte[] getBytes(URL url1) throws IOException {
		InputStream inputStream = url1.openStream();
		byte[] bytes = inputStream.readAllBytes();
		inputStream.close();
		return bytes;
	}
}
