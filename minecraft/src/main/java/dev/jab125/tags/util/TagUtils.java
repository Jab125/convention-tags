package dev.jab125.tags.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class TagUtils {
	public static TagKey<Biome> biome(String path) {
		return TagKey.create(Registries.BIOME, new ResourceLocation(path));
	}
	public static TagKey<Block> block(String path) {
		return TagKey.create(Registries.BLOCK, new ResourceLocation(path));
	}
	public static TagKey<Enchantment> enchantment(String path) {
		return TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(path));
	}
	public static TagKey<EntityType<?>> entityType(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(path));
	}
	public static TagKey<Fluid> fluid(String path) {
		return TagKey.create(Registries.FLUID, new ResourceLocation(path));
	}
	public static TagKey<Item> item(String path) {
		return TagKey.create(Registries.ITEM, new ResourceLocation(path));
	}
	public static TagKey<Structure> structure(String path) {
		return TagKey.create(Registries.STRUCTURE, new ResourceLocation(path));
	}
}
