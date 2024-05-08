package dev.jab125.tags;

import net.minecraft.tags.TagKey;
import dev.jab125.tags.util.TagUtils;
import net.minecraft.world.level.block.Block;

public class ConventionalBlockTags {
	private ConventionalBlockTags() {}

	public static final TagKey<Block> BARRELS = TagUtils.block("c:barrels");
	public static final TagKey<Block> WOODEN_BARRELS = TagUtils.block("c:barrels/wooden");
	public static final TagKey<Block> BOOKSHELVES = TagUtils.block("c:bookshelves");
	public static final TagKey<Block> BUDDING_BLOCKS = TagUtils.block("c:budding_blocks");
	public static final TagKey<Block> BUDS = TagUtils.block("c:buds");
	public static final TagKey<Block> CHAINS = TagUtils.block("c:chains");
	public static final TagKey<Block> CHESTS = TagUtils.block("c:chests");
	public static final TagKey<Block> ENDER_CHESTS = TagUtils.block("c:chests/ender");
	public static final TagKey<Block> TRAPPED_CHESTS = TagUtils.block("c:chests/trapped");
	public static final TagKey<Block> WOODEN_CHESTS = TagUtils.block("c:chests/wooden");
	public static final TagKey<Block> CLUSTERS = TagUtils.block("c:clusters");
	public static final TagKey<Block> COBBLESTONES = TagUtils.block("c:cobblestones");
	public static final TagKey<Block> DEEPSLATE_COBBLESTONES = TagUtils.block("c:cobblestones/deepslate");
	public static final TagKey<Block> INFESTED_COBBLESTONES = TagUtils.block("c:cobblestones/infested");
	public static final TagKey<Block> MOSSY_COBBLESTONES = TagUtils.block("c:cobblestones/mossy");
	public static final TagKey<Block> NORMAL_COBBLESTONES = TagUtils.block("c:cobblestones/normal");
	/**
	 * Contains all other dyed tags.
	 */
	public static final TagKey<Block> DYED = TagUtils.block("c:dyed");
	public static final TagKey<Block> BLACK_DYED = TagUtils.block("c:dyed/black");
	public static final TagKey<Block> BLUE_DYED = TagUtils.block("c:dyed/blue");
	public static final TagKey<Block> BROWN_DYED = TagUtils.block("c:dyed/brown");
	public static final TagKey<Block> CYAN_DYED = TagUtils.block("c:dyed/cyan");
	public static final TagKey<Block> GRAY_DYED = TagUtils.block("c:dyed/gray");
	public static final TagKey<Block> GREEN_DYED = TagUtils.block("c:dyed/green");
	public static final TagKey<Block> LIGHT_BLUE_DYED = TagUtils.block("c:dyed/light_blue");
	public static final TagKey<Block> LIGHT_GRAY_DYED = TagUtils.block("c:dyed/light_gray");
	public static final TagKey<Block> LIME_DYED = TagUtils.block("c:dyed/lime");
	public static final TagKey<Block> MAGENTA_DYED = TagUtils.block("c:dyed/magenta");
	public static final TagKey<Block> ORANGE_DYED = TagUtils.block("c:dyed/orange");
	public static final TagKey<Block> PINK_DYED = TagUtils.block("c:dyed/pink");
	public static final TagKey<Block> PURPLE_DYED = TagUtils.block("c:dyed/purple");
	public static final TagKey<Block> RED_DYED = TagUtils.block("c:dyed/red");
	public static final TagKey<Block> WHITE_DYED = TagUtils.block("c:dyed/white");
	public static final TagKey<Block> YELLOW_DYED = TagUtils.block("c:dyed/yellow");
	public static final TagKey<Block> END_STONES = TagUtils.block("c:end_stones");
	public static final TagKey<Block> FENCE_GATES = TagUtils.block("c:fence_gates");
	public static final TagKey<Block> WOODEN_FENCE_GATES = TagUtils.block("c:fence_gates/wooden");
	public static final TagKey<Block> FENCES = TagUtils.block("c:fences");
	public static final TagKey<Block> NETHER_BRICK_FENCES = TagUtils.block("c:fences/nether_brick");
	public static final TagKey<Block> WOODEN_FENCES = TagUtils.block("c:fences/wooden");
	public static final TagKey<Block> GRASS_BLOCKS = TagUtils.block("c:glass_blocks");
	/**
	 * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes.
	 */
	public static final TagKey<Block> CHEAP_GLASS_BLOCKS = TagUtils.block("c:glass_blocks/cheap");
	public static final TagKey<Block> COLORLESS_GLASS_BLOCKS = TagUtils.block("c:glass_blocks/colorless");
	public static final TagKey<Block> TINTED_GLASS_BLOCKS = TagUtils.block("c:glass_blocks/tinted");
	public static final TagKey<Block> GLASS_PANES = TagUtils.block("c:glass_panes");
	public static final TagKey<Block> COLORLESS_GLASS_PANES = TagUtils.block("c:glass_panes/colorless");
	public static final TagKey<Block> GRAVELS = TagUtils.block("c:gravels");
	/**
	 * This tag holds all blocks that should be hidden from recipe viewers.
	 */
	public static final TagKey<Block> HIDDEN_FROM_RECIPE_VIEWERS = TagUtils.block("c:hidden_from_recipe_viewers");
	public static final TagKey<Block> NETHERRACKS = TagUtils.block("c:netherracks");
	public static final TagKey<Block> OBSIDIANS = TagUtils.block("c:obsidians");
	/**
	 * Blocks that are treated like Deepslate during ore generation.
	 */
	public static final TagKey<Block> DEEPSLATE_ORE_BEARING_GROUND = TagUtils.block("c:ore_bearing_ground/deepslate");
	/**
	 * Blocks that are treated like Netherrack during ore generation.
	 */
	public static final TagKey<Block> NETHERRACK_ORE_BEARING_GROUND = TagUtils.block("c:ore_bearing_ground/netherrack");
	/**
	 * Blocks that are treated like Stone during ore generation.
	 */
	public static final TagKey<Block> STONE_ORE_BEARING_GROUND = TagUtils.block("c:ore_bearing_ground/stone");
	/**
	 * Ores that usually drop more than one resource when mined.
	 */
	public static final TagKey<Block> DENSE_ORE_RATES = TagUtils.block("c:ore_rates/dense");
	/**
	 * Ores that usually drop a single resource when mined.
	 */
	public static final TagKey<Block> SINGULAR_ORE_RATES = TagUtils.block("c:ore_rates/singular");
	/**
	 * Ores that usually drop less than one resource when mined.
	 */
	public static final TagKey<Block> SPARSE_ORE_RATES = TagUtils.block("c:ore_rates/sparse");
	public static final TagKey<Block> ORES = TagUtils.block("c:ores");
	public static final TagKey<Block> COAL_ORES = TagUtils.block("c:ores/coal");
	public static final TagKey<Block> COPPER_ORES = TagUtils.block("c:ores/copper");
	public static final TagKey<Block> DIAMOND_ORES = TagUtils.block("c:ores/diamond");
	public static final TagKey<Block> EMERALD_ORES = TagUtils.block("c:ores/emerald");
	public static final TagKey<Block> GOLD_ORES = TagUtils.block("c:ores/gold");
	public static final TagKey<Block> IRON_ORES = TagUtils.block("c:ores/iron");
	public static final TagKey<Block> LAPIS_ORES = TagUtils.block("c:ores/lapis");
	public static final TagKey<Block> NETHERITE_SCRAP_ORES = TagUtils.block("c:ores/netherite_scrap");
	public static final TagKey<Block> QUARTZ_ORES = TagUtils.block("c:ores/quartz");
	public static final TagKey<Block> REDSTONE_ORES = TagUtils.block("c:ores/redstone");
	public static final TagKey<Block> DEEPSLATE_ORES_IN_GROUND = TagUtils.block("c:ores_in_ground/deepslate");
	public static final TagKey<Block> NETHERRACK_ORES_IN_GROUND = TagUtils.block("c:ores_in_ground/netherrack");
	public static final TagKey<Block> STONE_ORES_IN_GROUND = TagUtils.block("c:ores_in_ground/stone");
	public static final TagKey<Block> PLAYER_WORKSTATIONS_CRAFTING_TABLES = TagUtils.block("c:player_workstations/crafting_tables");
	public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACES = TagUtils.block("c:player_workstations/furnaces");
	/**
	 * Blocks should be included in this tag if their movement can cause 
	 * serious issues such as world corruption or for balance reasons.
	 */
	public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = TagUtils.block("c:relocation_not_supported");
	public static final TagKey<Block> ROPES = TagUtils.block("c:ropes");
	public static final TagKey<Block> SANDS = TagUtils.block("c:sands");
	public static final TagKey<Block> COLORLESS_SANDS = TagUtils.block("c:sands/colorless");
	public static final TagKey<Block> RED_SANDS = TagUtils.block("c:sands/red");
	public static final TagKey<Block> SANDSTONE_BLOCKS = TagUtils.block("c:sandstone/blocks");
	public static final TagKey<Block> RED_SANDSTONE_BLOCKS = TagUtils.block("c:sandstone/red_blocks");
	public static final TagKey<Block> RED_SANDSTONE_SLABS = TagUtils.block("c:sandstone/red_slabs");
	public static final TagKey<Block> RED_SANDSTONE_STAIRS = TagUtils.block("c:sandstone/red_stairs");
	public static final TagKey<Block> SANDSTONE_SLABS = TagUtils.block("c:sandstone/slabs");
	public static final TagKey<Block> SANDSTONE_STAIRS = TagUtils.block("c:sandstone/stairs");
	public static final TagKey<Block> UNCOLORED_SANDSTONE_BLOCKS = TagUtils.block("c:sandstone/uncolored_blocks");
	public static final TagKey<Block> UNCOLORED_SANDSTONE_SLABS = TagUtils.block("c:sandstone/uncolored_slabs");
	public static final TagKey<Block> UNCOLORED_SANDSTONE_STAIRS = TagUtils.block("c:sandstone/uncolored_stairs");
	public static final TagKey<Block> SHULKER_BOXES = TagUtils.block("c:shulker_boxes");
	public static final TagKey<Block> SKULLS = TagUtils.block("c:skulls");
	public static final TagKey<Block> STONES = TagUtils.block("c:stones");
	public static final TagKey<Block> STORAGE_BLOCKS = TagUtils.block("c:storage_blocks");
	public static final TagKey<Block> BONE_MEAL_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/bone_meal");
	public static final TagKey<Block> COAL_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/coal");
	public static final TagKey<Block> COPPER_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/copper");
	public static final TagKey<Block> DIAMOND_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/diamond");
	public static final TagKey<Block> DRIED_KELP_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/dried_kelp");
	public static final TagKey<Block> EMERALD_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/emerald");
	public static final TagKey<Block> GOLD_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/gold");
	public static final TagKey<Block> IRON_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/iron");
	public static final TagKey<Block> LAPIS_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/lapis");
	public static final TagKey<Block> NETHERITE_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/netherite");
	public static final TagKey<Block> RAW_COPPER_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/raw_copper");
	public static final TagKey<Block> RAW_GOLD_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/raw_gold");
	public static final TagKey<Block> RAW_IRON_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/raw_iron");
	public static final TagKey<Block> REDSTONE_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/redstone");
	public static final TagKey<Block> SLIME_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/slime");
	public static final TagKey<Block> WHEAT_STORAGE_BLOCKS = TagUtils.block("c:storage_blocks/wheat");
	public static final TagKey<Block> VILLAGER_JOB_SITES = TagUtils.block("c:villager_job_sites");
}