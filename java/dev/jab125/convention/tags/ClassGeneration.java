package dev.jab125.convention.tags;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassGeneration {
	public static void main(String[] args) throws IOException {
		Path tagFile = Path.of("tags/output.tags");
		String s = Files.readString(tagFile);
		List<Tag> tags = Tag.deserialize(s);
		// import net.minecraft.world.entity.EntityType;
		//import net.minecraft.world.item.Item;
		//import net.minecraft.world.item.enchantment.Enchantment;
		//import net.minecraft.world.level.biome.Biome;
		//import net.minecraft.world.level.block.Block;
		//import net.minecraft.world.level.levelgen.structure.Structure;
		//import net.minecraft.world.level.material.Fluid;
		Map<String, String> typeToClass = Map.of(
				"minecraft:worldgen/biome",     "net.minecraft.world.level.biome.Biome",
				"minecraft:block",              "net.minecraft.world.level.block.Block",
				"minecraft:enchantment",        "net.minecraft.world.item.enchantment.Enchantment",
				"minecraft:entity_type",        "net.minecraft.world.entity.EntityType",
				"minecraft:fluid",              "net.minecraft.world.level.material.Fluid",
				"minecraft:item",               "net.minecraft.world.item.Item",
				"minecraft:worldgen/structure", "net.minecraft.world.level.levelgen.structure.Structure"
		);

		Map<String, String> typeToMethod = Map.of(
				"minecraft:worldgen/biome",     "biome",
				"minecraft:block",              "block",
				"minecraft:enchantment",        "enchantment",
				"minecraft:entity_type",        "entityType",
				"minecraft:fluid",              "fluid",
				"minecraft:item",               "item",
				"minecraft:worldgen/structure", "structure"
		);
		Map<String, String> r = new HashMap<>();
		for (Tag tag : tags) {
			r.computeIfAbsent(tag.registryKey(), registryKey -> {
				String clazz = tag.convention().clazz();
				String p = "package " + clazz.substring(0, clazz.lastIndexOf("/")).replace('/', '.') + ";\n";
				p += "\n";
				p += "import net.minecraft.tags.TagKey;\n";
				p += "import dev.jab125.tags.util.TagUtils;\n";
				p += "import " + typeToClass.get(tag.registryKey()) + ";\n";
				p += "\n";
				p += "public class " + clazz.substring(clazz.lastIndexOf("/")+1) + " {\n";
				p += "\tprivate " + clazz.substring(clazz.lastIndexOf("/")+1) + "() {}\n\n";
				return p;
			});
			String s1 = r.get(tag.registryKey());
			String importedClassName = typeToClass.get(tag.registryKey()).substring(typeToClass.get(tag.registryKey()).lastIndexOf(".") + 1);
			if (importedClassName.equals("EntityType")) importedClassName += "<?>";
			String[] comments = tag.comments();
			if (comments.length > 0) {
				s1 += "\t/**\n";
				for (String comment : comments) {
					s1 += "\t * " + comment + "\n";
				}
				s1 += "\t */\n";
			}
			s1 += "\tpublic static final TagKey<" + importedClassName + "> " + tag.convention().method() + " = TagUtils." + typeToMethod.get(tag.registryKey()) + "(\"" + tag.name() + "\");\n";
			r.put(tag.registryKey(), s1);
		}
		r.forEach((a, b) -> r.put(a, b + "}"));
		r.forEach((a, b) -> {
			Path of = Path.of("minecraft/src/main/generated/java/dev/jab125/tags");
			of.toFile().mkdirs();
			try {
				Files.writeString(of.resolve(tags.stream().filter(c -> c.registryKey().equals(a)).findFirst().get().convention().clazz().substring(tags.stream().filter(c -> c.registryKey().equals(a)).findFirst().get().convention().clazz().lastIndexOf("/") + 1) + ".java"), b);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
