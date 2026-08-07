package fr.skyost.owngarden.config;

import fr.skyost.owngarden.model.DecorationBlock;
import fr.skyost.owngarden.model.DecorationPattern;
import fr.skyost.owngarden.util.Skyoconfig;

import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.structure.Structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

	@ConfigOptions(name = "decoration-patterns")
	public HashMap<String, HashMap<String, Object>> decorationPatterns = new HashMap<>();

	@ConfigOptions(name = "decorations")
	public HashMap<String, HashMap<String, String>> decorations = new HashMap<>();

	// TreeType -> (biomeGroup -> structures)
	private HashMap<TreeType, HashMap<String, List<Structure>>> loadedTreeTypeStructures;

	@ConfigOptions(ignore = true)
	private HashMap<String, DecorationPattern> loadedDecorationPatterns = new HashMap<>();

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
		// Default values are now loaded from src/main/resources/config.yml
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

	/**
	 * Loads decoration patterns from config into usable objects.
	 */
	@SuppressWarnings("unchecked")
	public void loadDecorationPatterns() {
		this.loadedDecorationPatterns.clear();

		for (Map.Entry<String, HashMap<String, Object>> entry : decorationPatterns.entrySet()) {
			String name = entry.getKey();
			DecorationPattern pattern = parseDecorationPattern(name, entry.getValue());
			if (pattern != null) {
				this.loadedDecorationPatterns.put(name, pattern);
			}
		}
	}

	/**
	 * Parses a decoration pattern from config data.
	 *
	 * @param name The pattern name
	 * @param data The config data map
	 * @return The parsed DecorationPattern, or null if invalid
	 */
	@SuppressWarnings("unchecked")
	private DecorationPattern parseDecorationPattern(String name, Map<String, Object> data) {
		try {
			int minRadius = getIntValue(data.get("min-radius"), 2);
			int maxRadius = getIntValue(data.get("max-radius"), 4);
			int maxVerticalSearch = getIntValue(data.get("max-vertical-search"), 5);

			List<String> replaceableBlocks = (List<String>) data.getOrDefault("replaceable-blocks",
					List.of("AIR", "CAVE_AIR", "SHORT_GRASS"));
			List<String> placeableOnBlocks = (List<String>) data.getOrDefault("placeable-on-blocks",
					List.of("GRASS_BLOCK", "DIRT", "PODZOL"));

			List<DecorationBlock> blocks = new ArrayList<>();
			Object blocksData = data.get("blocks");
			if (blocksData instanceof List) {
				for (Object blockObj : (List<?>) blocksData) {
					if (blockObj instanceof Map) {
						Map<String, Object> blockMap = (Map<String, Object>) blockObj;
						String material = (String) blockMap.get("material");
						double chance = getDoubleValue(blockMap.get("chance"), 0.1);
						boolean randomRotation = getBooleanValue(blockMap.get("random-rotation"), false);
						int height = getIntValue(blockMap.get("height"), 1);

						if (material != null) {
							blocks.add(new DecorationBlock(material, chance, randomRotation, height));
						}
					}
				}
			}

			return new DecorationPattern(name, minRadius, maxRadius, maxVerticalSearch,
					blocks, replaceableBlocks, placeableOnBlocks);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets a single decoration pattern for a tree type and biome.
	 * For backwards compatibility - returns only the first pattern if multiple are configured.
	 *
	 * @param treeType The tree type
	 * @param biome    The biome
	 * @return The decoration pattern, or null if none configured
	 */
	public DecorationPattern getDecorationPattern(TreeType treeType, Biome biome) {
		List<DecorationPattern> patterns = getDecorationPatterns(treeType, biome);
		return patterns.isEmpty() ? null : patterns.get(0);
	}

	/**
	 * Gets all decoration patterns for a tree type and biome.
	 * Supports comma-separated pattern lists (e.g., "dark-oak-forest,mushroom-sparse").
	 *
	 * @param treeType The tree type
	 * @param biome    The biome
	 * @return List of decoration patterns, empty if none configured
	 */
	public List<DecorationPattern> getDecorationPatterns(TreeType treeType, Biome biome) {
		String treeName = getTreeTypeName(treeType);
		HashMap<String, String> treeDecorations = decorations.get(treeName);
		if (treeDecorations == null) {
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

		// Try to get pattern for the matched biome group
		String patternNames = null;
		if (matchedGroup != null) {
			patternNames = treeDecorations.get(matchedGroup);
		}

		// Fallback to default
		if (patternNames == null || patternNames.equalsIgnoreCase("null")) {
			patternNames = treeDecorations.get("default");
		}

		if (patternNames == null || patternNames.equalsIgnoreCase("null")) {
			return Collections.emptyList();
		}

		// Support comma-separated pattern lists
		if (patternNames.contains(",")) {
			return Arrays.stream(patternNames.split(","))
				.map(String::trim)
				.map(loadedDecorationPatterns::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		}

		DecorationPattern pattern = loadedDecorationPatterns.get(patternNames);
		return pattern != null ? List.of(pattern) : Collections.emptyList();
	}

	/**
	 * Gets all loaded decoration pattern names.
	 *
	 * @return List of pattern names
	 */
	public List<String> getDecorationPatternNames() {
		return new ArrayList<>(loadedDecorationPatterns.keySet());
	}

	private int getIntValue(Object value, int defaultValue) {
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		return defaultValue;
	}

	private double getDoubleValue(Object value, double defaultValue) {
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		return defaultValue;
	}

	private boolean getBooleanValue(Object value, boolean defaultValue) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		return defaultValue;
	}

}