package dev.jab125.tags;

import net.minecraft.tags.TagKey;
import dev.jab125.tags.util.TagUtils;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTags {
	private EntityTypeTags() {}

	public static final TagKey<EntityType<?>> BOATS = TagUtils.entityType("c:boats");
	public static final TagKey<EntityType<?>> BOSSES = TagUtils.entityType("c:bosses");
	public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = TagUtils.entityType("c:capturing_not_supported");
	public static final TagKey<EntityType<?>> MINECARTS = TagUtils.entityType("c:minecarts");
	public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = TagUtils.entityType("c:teleporting_not_supported");
}