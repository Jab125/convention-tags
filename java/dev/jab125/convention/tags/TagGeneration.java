package dev.jab125.convention.tags;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TagGeneration {
	public static final boolean legacyNames = true; // Set to false in 24w19a+
	public static final String FABRIC_CONVENTION_TAGS_VERSION = "2.1.0+619abec268";
	public static final String NEOFORGE_VERSION = null;
	public static final Path GENERATION = Path.of("generation");
	public static final List<Tag.TagBuilder> mutableTags = new ArrayList<>();
	private static final Path NEOFORGE_FILE = GENERATION.resolve("neoforge.jar");
	private static final Path FABRIC_FILE = GENERATION.resolve("fabric.jar");
	private static final Path NEOFORGE_DIR = GENERATION.resolve("neoforge");
	private static final Path FABRIC_DIR = GENERATION.resolve("fabric");
	private static final Path NEOFORGE_CLASSES_DIR = NEOFORGE_DIR.resolve("classes");
	private static final Path FABRIC_CLASSES_DIR = FABRIC_DIR.resolve("classes");
	private static final Path NEOFORGE_DATA_DIR = NEOFORGE_DIR.resolve("data");
	private static final Path FABRIC_DATA_DIR = FABRIC_DIR.resolve("data");

	@SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
	public static void main(String[] args) throws IOException {
		try (final Stopwatch total = new Stopwatch("Total")) {
			try (final Stopwatch dirs = new Stopwatch("Create Directories")) {
				GENERATION.toFile().mkdirs();
				NEOFORGE_CLASSES_DIR.toFile().mkdirs();
				FABRIC_CLASSES_DIR.toFile().mkdirs();
				NEOFORGE_DATA_DIR.toFile().mkdirs();
				FABRIC_DATA_DIR.toFile().mkdirs();
			}
			try (final Stopwatch download = new Stopwatch("Download")) {
				if (NEOFORGE_VERSION != null) downloadNeoForge();
				if (FABRIC_CONVENTION_TAGS_VERSION != null) downloadFabric();
			}
			try (final Stopwatch extract = new Stopwatch("ZIP Extract")) {
				if (NEOFORGE_VERSION != null) extractNeoForge();
				if (FABRIC_CONVENTION_TAGS_VERSION != null) extractFabric();
			}
			try (final Stopwatch visit = new Stopwatch("Visit Classes")) {
				if (NEOFORGE_VERSION != null) visitNeoForgeClasses();
				if (FABRIC_CONVENTION_TAGS_VERSION != null) visitFabricClasses();
			}
			try (final Stopwatch visit = new Stopwatch("Visit Data")) {
				if (FABRIC_CONVENTION_TAGS_VERSION != null) visitFabricData(); // we want Fabric first then NeoForge
				if (NEOFORGE_VERSION != null) visitNeoForgeData();
			}
			try (final Stopwatch generate = new Stopwatch("Generate")) {
				List<Tag> tags = new ArrayList<>();
				for (Tag.TagBuilder mutableTag : mutableTags) {
					tags.add(mutableTag.build());
				}
			}
		}
	}

	private static void visitFabricData() throws IOException {
		Files.walkFileTree(FABRIC_DATA_DIR, new DataTagsVisitor(Ecosystem.FABRIC, FABRIC_DATA_DIR));
	}

	private static void visitNeoForgeData() throws IOException {
		Files.walkFileTree(NEOFORGE_DATA_DIR, new DataTagsVisitor(Ecosystem.NEOFORGE, NEOFORGE_DATA_DIR));
	}

	public static void downloadNeoForge() throws IOException {
		String url = "https://maven.neoforged.net/releases/net/neoforged/neoforge/%s/neoforge-%s-universal.jar".formatted(NEOFORGE_VERSION, NEOFORGE_VERSION);
		URL url1 = new URL(url);
		byte[] bytes = getBytes(url1);
		Files.write(NEOFORGE_FILE, bytes);
	}

	public static void downloadFabric() throws IOException {
		String url = "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-convention-tags-v2/%s/fabric-convention-tags-v2-%s.jar".formatted(FABRIC_CONVENTION_TAGS_VERSION, FABRIC_CONVENTION_TAGS_VERSION);
		URL url1 = new URL(url);
		byte[] bytes = getBytes(url1);
		Files.write(FABRIC_FILE, bytes);
	}
	public static void extractNeoForge() throws IOException {
		try (ZipFile zf = new ZipFile(NEOFORGE_FILE.toFile())) {
			for (Iterator<? extends ZipEntry> it = zf.entries().asIterator(); it.hasNext(); ) {
				var next = it.next();
				if (next.isDirectory()) continue;
				if (next.getName().startsWith("net/neoforged/neoforge/common/Tags")) {
					InputStream in = zf.getInputStream(zf.getEntry(next.getName()));
					Files.write(NEOFORGE_CLASSES_DIR.resolve(next.getName().substring(30)), in.readAllBytes());
				}
				if (next.getName().startsWith("data/c/tags/")) {
					InputStream in = zf.getInputStream(zf.getEntry(next.getName()));
					NEOFORGE_DATA_DIR.resolve(next.getName().substring(12)).getParent().toFile().mkdirs();
					Files.write(NEOFORGE_DATA_DIR.resolve(next.getName().substring(12)), in.readAllBytes());
				}
			}
		}
	}
	public static void extractFabric() throws IOException {
		try (ZipFile zf = new ZipFile(FABRIC_FILE.toFile())) {
			for (Iterator<? extends ZipEntry> it = zf.entries().asIterator(); it.hasNext(); ) {
				var next = it.next();
				if (next.isDirectory()) continue;
				if (next.getName().startsWith("net/fabricmc/fabric/api/tag/convention/v2/Conventional")) {
					InputStream in = zf.getInputStream(zf.getEntry(next.getName()));
					Files.write(FABRIC_CLASSES_DIR.resolve(next.getName().substring(42)), in.readAllBytes());
				}
				if (next.getName().startsWith("data/c/tags/")) {
					InputStream in = zf.getInputStream(zf.getEntry(next.getName()));
					FABRIC_DATA_DIR.resolve(next.getName().substring(12)).getParent().toFile().mkdirs();
					Files.write(FABRIC_DATA_DIR.resolve(next.getName().substring(12)), in.readAllBytes());
				}
			}
		}
	}
	@SuppressWarnings("DataFlowIssue")
	public static void visitNeoForgeClasses() throws IOException {
		//new ClassReader()
		for (File file : NEOFORGE_CLASSES_DIR.toFile().listFiles()) {
			if (!file.getName().endsWith(".class")) continue;
			String type = switch (file.getName()) {
				case "Tags$Biomes.class" -> "minecraft:worldgen/biome";
				case "Tags$Blocks.class" -> "minecraft:block";
				case "Tags$DamageTypes.class" -> "pass"; // there aren't any common damage types
				case "Tags$Enchantments.class" -> "minecraft:enchantment";
				case "Tags$EntityTypes.class" -> "minecraft:entity_type";
				case "Tags$Fluids.class" -> "minecraft:fluid";
				case "Tags$Items.class" -> "minecraft:item";
				case "Tags$Structures.class" -> "minecraft:worldgen/structure";
				default -> null;
			};
			ClassReader classReader = new ClassReader(Files.readAllBytes(file.toPath()));
			ClassNode classNode = new ClassNode();
			classReader.accept(classNode, 0);
			for (MethodNode method : classNode.methods) {
				for (AbstractInsnNode instruction : method.instructions) {
					if (instruction instanceof MethodInsnNode methodInsnNode) {
						if (methodInsnNode.name.equals("tag") && methodInsnNode.desc.equals("(Ljava/lang/String;)Lnet/minecraft/tags/TagKey;")) {
							AbstractInsnNode previous = methodInsnNode.getPrevious();
							if (previous instanceof LdcInsnNode ldcInsnNode) {
								Object cst = ldcInsnNode.cst;
								if (!(cst instanceof String str)) continue;
								if (!(methodInsnNode.getNext() instanceof FieldInsnNode fieldInsnNode)) continue;
								String tagName = "c:" + str;
								getOrCreate(type, tagName).field(Ecosystem.NEOFORGE, classNode.name, fieldInsnNode.name);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("DataFlowIssue")
	public static void visitFabricClasses() throws IOException {
		//new ClassReader()
		for (File file : FABRIC_CLASSES_DIR.toFile().listFiles()) {
			if (!file.getName().endsWith(".class")) continue;
			String type = switch (file.getName()) {
				case "ConventionalBiomeTags.class" -> "minecraft:worldgen/biome";
				case "ConventionalBlockTags.class" -> "minecraft:block";
				case "ConventionalDamageTypeTags.class" -> "pass"; // there aren't any common damage types
				case "ConventionalEnchantmentTags.class" -> "minecraft:enchantment";
				case "ConventionalEntityTypeTags.class" -> "minecraft:entity_type";
				case "ConventionalFluidTags.class" -> "minecraft:fluid";
				case "ConventionalItemTags.class" -> "minecraft:item";
				case "ConventionalStructureTags.class" -> "minecraft:worldgen/structure";
				default -> null;
			};
			ClassReader classReader = new ClassReader(Files.readAllBytes(file.toPath()));
			ClassNode classNode = new ClassNode();
			classReader.accept(classNode, 0);
			for (MethodNode method : classNode.methods) {
				for (AbstractInsnNode instruction : method.instructions) {
					if (instruction instanceof MethodInsnNode methodInsnNode) {
						if (methodInsnNode.name.equals("register") && methodInsnNode.desc.equals("(Ljava/lang/String;)Lnet/minecraft/class_6862;")) {
							AbstractInsnNode previous = methodInsnNode.getPrevious();
							if (previous instanceof LdcInsnNode ldcInsnNode) {
								Object cst = ldcInsnNode.cst;
								if (!(cst instanceof String str)) continue;
								if (!(methodInsnNode.getNext() instanceof FieldInsnNode fieldInsnNode)) continue;
								String tagName = "c:" + str;
								getOrCreate(type, tagName).field(Ecosystem.FABRIC, classNode.name, fieldInsnNode.name);
							}
						}
					}
				}
			}
		}
	}
	private static Tag.TagBuilder getOrCreate(String registryKey, String tagName) {
		Optional<Tag.TagBuilder> first = mutableTags.stream().filter(a -> a.getName().equals(tagName) && a.getRegistryKey().equals(registryKey)).findFirst();
		if (first.isPresent()) return first.get();
		Tag.TagBuilder builder = new Tag.TagBuilder().name(registryKey, tagName);
		mutableTags.add(builder);
		return builder;
	}

	private static byte[] getBytes(URL url1) throws IOException {
		InputStream inputStream = url1.openStream();
		byte[] bytes = inputStream.readAllBytes();
		inputStream.close();
		return bytes;
	}

	private static class DataTagsVisitor extends SimpleFileVisitor<Path> {

		private final Path dir;
		private final Ecosystem ecosystem;

		public DataTagsVisitor(Ecosystem ecosystem, Path dir) {
			this.ecosystem = ecosystem;
			this.dir = dir;
		}
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Path relativePath = dir.relativize(file);
			int count = 0;
			String type = "";
			String tagName = "c:";
			for (Iterator<Path> it = relativePath.iterator(); it.hasNext(); ) {
				Path next = it.next();
				if (count == 0) {
					type = "minecraft:" + next.toString();
				} else if (count == 1 && type.equals("minecraft:worldgen")) {
					type += "/" + next.toString();
				} else {
					tagName += next.toString() + "/";
				}
				count++;
			}
			type = fix(type);
			tagName = tagName.substring(0, tagName.length() - 6);
			JsonObject object = new Gson().fromJson(Files.readString(file, StandardCharsets.UTF_8), JsonObject.class);
			for (JsonElement value : object.getAsJsonArray("values")) {
				if (value.isJsonPrimitive()) {
					getOrCreate(type, tagName).entry(ecosystem, value.getAsString(), false);
				} else if (value.isJsonObject()) {
					String name = value.getAsJsonObject().getAsJsonPrimitive("id").getAsString();
					boolean required = value.getAsJsonObject().getAsJsonPrimitive("required") != null && value.getAsJsonObject().getAsJsonPrimitive("required").getAsBoolean();
					if (!required) {
						if (name.startsWith("#")) name = name.substring(1);
						if (name.startsWith("c:") || name.startsWith("forge:")) continue; // these are probably legacy tags
					}
					getOrCreate(type, tagName).entry(ecosystem, name, required);
				} else {
					throw new RuntimeException();
				}
			}
			return super.visitFile(file, attrs);
		}

		private String fix(String type) {
			if (!legacyNames) return type;
			if (type.equals("minecraft:blocks")) return "minecraft:block";
			if (type.equals("minecraft:fluids")) return "minecraft:fluid";
			if (type.equals("minecraft:items")) return "minecraft:item";
			if (type.equals("minecraft:entity_types")) return "minecraft:entity_type";
			if (type.equals("minecraft:game_events")) return "minecraft:game_event";
			return type;
		}
	}
}
