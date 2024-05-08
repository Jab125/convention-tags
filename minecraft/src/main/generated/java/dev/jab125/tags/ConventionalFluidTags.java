package dev.jab125.tags;

import net.minecraft.tags.TagKey;
import dev.jab125.tags.util.TagUtils;
import net.minecraft.world.level.material.Fluid;

public class ConventionalFluidTags {
	private ConventionalFluidTags() {}

	public static final TagKey<Fluid> BEETROOT_SOUP = TagUtils.fluid("c:beetroot_soup");
	/**
	 * Liquids that flow up, instead of down.
	 */
	public static final TagKey<Fluid> GASEOUS = TagUtils.fluid("c:gaseous");
	public static final TagKey<Fluid> HIDDEN_FROM_RECIPE_VIEWERS = TagUtils.fluid("c:hidden_from_recipe_viewers");
	public static final TagKey<Fluid> HONEY = TagUtils.fluid("c:honey");
	public static final TagKey<Fluid> LAVA = TagUtils.fluid("c:lava");
	public static final TagKey<Fluid> MILK = TagUtils.fluid("c:milk");
	public static final TagKey<Fluid> MUSHROOM_STEW = TagUtils.fluid("c:mushroom_stew");
	public static final TagKey<Fluid> POTION = TagUtils.fluid("c:potion");
	public static final TagKey<Fluid> RABBIT_STEW = TagUtils.fluid("c:rabbit_stew");
	public static final TagKey<Fluid> SUSPICIOUS_STEW = TagUtils.fluid("c:suspicious_stew");
	public static final TagKey<Fluid> WATER = TagUtils.fluid("c:water");
}