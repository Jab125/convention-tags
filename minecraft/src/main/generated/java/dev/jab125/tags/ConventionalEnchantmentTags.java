package dev.jab125.tags;

import net.minecraft.tags.TagKey;
import dev.jab125.tags.util.TagUtils;
import net.minecraft.world.item.enchantment.Enchantment;

public class ConventionalEnchantmentTags {
	private ConventionalEnchantmentTags() {}

	public static final TagKey<Enchantment> ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS = TagUtils.enchantment("c:entity_auxiliary_movement_enhancements");
	public static final TagKey<Enchantment> ENTITY_DEFENSE_ENHANCEMENTS = TagUtils.enchantment("c:entity_defense_enhancements");
	public static final TagKey<Enchantment> ENTITY_SPEED_ENHANCEMENTS = TagUtils.enchantment("c:entity_speed_enhancements");
	public static final TagKey<Enchantment> INCREASE_BLOCK_DROPS = TagUtils.enchantment("c:increase_block_drops");
	public static final TagKey<Enchantment> INCREASE_ENTITY_DROPS = TagUtils.enchantment("c:increase_entity_drops");
	public static final TagKey<Enchantment> WEAPON_DAMAGE_ENHANCEMENTS = TagUtils.enchantment("c:weapon_damage_enhancements");
}