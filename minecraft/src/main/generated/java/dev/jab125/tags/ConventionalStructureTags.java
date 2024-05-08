package dev.jab125.tags;

import net.minecraft.tags.TagKey;
import dev.jab125.tags.util.TagUtils;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ConventionalStructureTags {
	private ConventionalStructureTags() {}

	public static final TagKey<Structure> HIDDEN_FROM_DISPLAYERS = TagUtils.structure("c:hidden_from_displayers");
	public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = TagUtils.structure("c:hidden_from_locator_selection");
}