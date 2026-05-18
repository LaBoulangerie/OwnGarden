package fr.skyost.owngarden.config;

import fr.skyost.owngarden.util.Skyoconfig;

import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.structure.Structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The plugin configuration.
 */

public class PluginConfig extends Skyoconfig {

	@ConfigOptions(name = "structures.directory")
	public String structuresDirectory;

	@ConfigOptions(name = "structures.random-rotation")
	public boolean structuresRandomRotation = true;

	@ConfigOptions(name = "structures.height-check.enabled")
	public boolean structuresHeightCheckEnabled = true;

	@ConfigOptions(name = "structures.height-check.on-fail")
	public HashMap<String, String> structuresHeightCheckOnFail = new HashMap<>();

	@ConfigOptions(name = "structures.biome-groups")
	public HashMap<String, List<String>> structuresBiomeGroups = new HashMap<>();

	// TreeType -> (biomeGroup -> structures)
	private HashMap<TreeType, HashMap<String, List<Structure>>> loadedTreeTypeStructures;

	/**
	 * Creates a new plugin config instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */

	/**
	 * Behavior when height check fails.
	 */
	public enum HeightCheckFailBehavior {
		CANCEL,  // Don't grow anything, sapling stays
		VANILLA  // Let vanilla tree grow
	}

	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Collections.singletonList("OwnGarden Configuration File"));

		structuresDirectory = new File(dataFolder, "structures/").getPath();
		this.loadedTreeTypeStructures = new HashMap<>();

		// Default height check behavior for each tree type
		structuresHeightCheckOnFail.put("oak", "VANILLA");
		structuresHeightCheckOnFail.put("big_oak", "VANILLA");
		structuresHeightCheckOnFail.put("spruce", "VANILLA");
		structuresHeightCheckOnFail.put("birch", "VANILLA");
		structuresHeightCheckOnFail.put("jungle", "VANILLA");
		structuresHeightCheckOnFail.put("acacia", "VANILLA");
		structuresHeightCheckOnFail.put("dark_oak", "VANILLA");
		structuresHeightCheckOnFail.put("mega_redwood", "VANILLA");
		structuresHeightCheckOnFail.put("small_jungle", "VANILLA");
		structuresHeightCheckOnFail.put("brown_mushroom", "VANILLA");
		structuresHeightCheckOnFail.put("red_mushroom", "VANILLA");

		// Default biome groups
		List<String> forestBiomes = new ArrayList<>();
		forestBiomes.add("FOREST");
		forestBiomes.add("FLOWER_FOREST");
		forestBiomes.add("BIRCH_FOREST");
		forestBiomes.add("OLD_GROWTH_BIRCH_FOREST");
		forestBiomes.add("DARK_FOREST");
		forestBiomes.add("PALE_GARDEN");
		forestBiomes.add("CHERRY_GROVE");
		structuresBiomeGroups.put("forest", forestBiomes);

		List<String> plainsBiomes = new ArrayList<>();
		plainsBiomes.add("PLAINS");
		plainsBiomes.add("SUNFLOWER_PLAINS");
		plainsBiomes.add("MEADOW");
		structuresBiomeGroups.put("plains", plainsBiomes);

		List<String> coldBiomes = new ArrayList<>();
		coldBiomes.add("TAIGA");
		coldBiomes.add("OLD_GROWTH_PINE_TAIGA");
		coldBiomes.add("OLD_GROWTH_SPRUCE_TAIGA");
		coldBiomes.add("SNOWY_TAIGA");
		coldBiomes.add("SNOWY_PLAINS");
		coldBiomes.add("SNOWY_BEACH");
		coldBiomes.add("SNOWY_SLOPES");
		coldBiomes.add("ICE_SPIKES");
		coldBiomes.add("FROZEN_PEAKS");
		coldBiomes.add("FROZEN_OCEAN");
		coldBiomes.add("DEEP_FROZEN_OCEAN");
		coldBiomes.add("FROZEN_RIVER");
		coldBiomes.add("GROVE");
		structuresBiomeGroups.put("cold", coldBiomes);

		List<String> jungleBiomes = new ArrayList<>();
		jungleBiomes.add("JUNGLE");
		jungleBiomes.add("SPARSE_JUNGLE");
		jungleBiomes.add("BAMBOO_JUNGLE");
		structuresBiomeGroups.put("jungle", jungleBiomes);

		List<String> savannaBiomes = new ArrayList<>();
		savannaBiomes.add("SAVANNA");
		savannaBiomes.add("SAVANNA_PLATEAU");
		savannaBiomes.add("WINDSWEPT_SAVANNA");
		structuresBiomeGroups.put("savanna", savannaBiomes);

		List<String> swampBiomes = new ArrayList<>();
		swampBiomes.add("SWAMP");
		swampBiomes.add("MANGROVE_SWAMP");
		structuresBiomeGroups.put("swamp", swampBiomes);

		List<String> hotBiomes = new ArrayList<>();
		hotBiomes.add("DESERT");
		hotBiomes.add("BADLANDS");
		hotBiomes.add("WOODED_BADLANDS");
		hotBiomes.add("ERODED_BADLANDS");
		structuresBiomeGroups.put("hot", hotBiomes);

		List<String> mountainsBiomes = new ArrayList<>();
		mountainsBiomes.add("WINDSWEPT_HILLS");
		mountainsBiomes.add("WINDSWEPT_FOREST");
		mountainsBiomes.add("WINDSWEPT_GRAVELLY_HILLS");
		mountainsBiomes.add("JAGGED_PEAKS");
		mountainsBiomes.add("STONY_PEAKS");
		structuresBiomeGroups.put("mountains", mountainsBiomes);

		List<String> netherBiomes = new ArrayList<>();
		netherBiomes.add("NETHER_WASTES");
		netherBiomes.add("SOUL_SAND_VALLEY");
		netherBiomes.add("CRIMSON_FOREST");
		netherBiomes.add("WARPED_FOREST");
		netherBiomes.add("BASALT_DELTAS");
		structuresBiomeGroups.put("nether", netherBiomes);

		List<String> endBiomes = new ArrayList<>();
		endBiomes.add("THE_END");
		endBiomes.add("SMALL_END_ISLANDS");
		endBiomes.add("END_MIDLANDS");
		endBiomes.add("END_HIGHLANDS");
		endBiomes.add("END_BARRENS");
		structuresBiomeGroups.put("end", endBiomes);
	}

	public final String getTreeTypeName(final TreeType treeType) {
		switch (treeType) {
			case TREE:
				return "oak";
			case BIG_TREE:
				return "big_oak";
			default:
				return treeType.name().toLowerCase();
		}
	}

	public final File getTreeStructuresDirectory(final TreeType treeType) {
		return new File(this.structuresDirectory, this.getTreeTypeName(treeType));
	}

	public final void setTreeTypeStructures(final TreeType treeType, final String biomeGroup, final List<Structure> structures) {
		if (!this.loadedTreeTypeStructures.containsKey(treeType)) {
			this.loadedTreeTypeStructures.put(treeType, new HashMap<>());
		}
		this.loadedTreeTypeStructures.get(treeType).put(biomeGroup, structures);
	}

	public final List<Structure> getTreeTypeStructures(TreeType treeType, Biome biome) {
		HashMap<String, List<Structure>> biomeStructures = this.loadedTreeTypeStructures.get(treeType);
		if (biomeStructures == null) {
			return Collections.emptyList();
		}

		// Find the biome group for this biome
		String biomeName = biome.name();
		String matchedGroup = null;
		for (var entry : structuresBiomeGroups.entrySet()) {
			if (entry.getValue().contains(biomeName)) {
				matchedGroup = entry.getKey();
				break;
			}
		}

		// Try to get structures for the matched biome group
		if (matchedGroup != null && biomeStructures.containsKey(matchedGroup)) {
			List<Structure> structures = biomeStructures.get(matchedGroup);
			if (structures != null && !structures.isEmpty()) {
				return structures;
			}
		}

		// Fallback to default
		List<Structure> defaultStructures = biomeStructures.get("default");
		if (defaultStructures != null) {
			return defaultStructures;
		}

		return Collections.emptyList();
	}

	public final List<Structure> getTreeTypeStructures(TreeType treeType) {
		// Legacy method - returns all structures combined
		HashMap<String, List<Structure>> biomeStructures = this.loadedTreeTypeStructures.get(treeType);
		if (biomeStructures == null) {
			return Collections.emptyList();
		}
		List<Structure> allStructures = new ArrayList<>();
		for (List<Structure> structures : biomeStructures.values()) {
			allStructures.addAll(structures);
		}
		return allStructures;
	}

	public final HeightCheckFailBehavior getHeightCheckFailBehavior(TreeType treeType) {
		String behavior = structuresHeightCheckOnFail.get(getTreeTypeName(treeType));
		if (behavior == null) {
			return HeightCheckFailBehavior.VANILLA;
		}
		try {
			return HeightCheckFailBehavior.valueOf(behavior.toUpperCase());
		} catch (IllegalArgumentException e) {
			return HeightCheckFailBehavior.VANILLA;
		}
	}

	/**
	 * Clears all loaded structures from the cache.
	 * Used when reloading the plugin configuration.
	 */
	public void clearLoadedStructures() {
		this.loadedTreeTypeStructures.clear();
	}

}