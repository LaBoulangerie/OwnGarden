package fr.skyost.owngarden.config;

import fr.skyost.owngarden.util.Skyoconfig;
import org.bukkit.Material;
import org.bukkit.TreeType;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The plugin configuration.
 */

public class PluginConfig extends Skyoconfig {

	@ConfigOptions(name = "enable.updater")
	public boolean enableUpdater = true;

	@ConfigOptions(name = "enable.metrics")
	public boolean enableMetrics = true;

	@ConfigOptions(name = "structures.directory")
	public String structuresDirectory;

	@ConfigOptions(name = "structures.random-rotation")
	public boolean structuresRandomRotation = true;

	@ConfigOptions(name = "structures.check-height")
	public boolean structuresCheckHeight = false;

	@ConfigOptions(name = "structures.remove-worldedit-metadata")
	public boolean structuresRemoveWorldEditMetaData = true;

	@ConfigOptions(name = "sapling.oak")
	public List<String> saplingOakStructures = Arrays.asList("oak/1.structure", "oak/2.structure", "oak/3.structure");

	@ConfigOptions(name = "sapling.spruce")
	public List<String> saplingSpruceStructures = Arrays.asList("spruce/1.structure", "spruce/2.structure",
			"spruce/3.structure");

	@ConfigOptions(name = "sapling.birch")
	public List<String> saplingBirchStructures = Arrays.asList("birch/1.structure", "birch/2.structure",
			"birch/3.structure");

	@ConfigOptions(name = "sapling.jungle")
	public List<String> saplingJungleStructures = Arrays.asList("jungle/1.structure", "jungle/2.structure",
			"jungle/3.structure");

	@ConfigOptions(name = "sapling.acacia")
	public List<String> saplingAcaciaStructures = Arrays.asList("acacia/1.structure", "acacia/2.structure",
			"acacia/3.structure");

	@ConfigOptions(name = "sapling.dark-oak")
	public List<String> saplingDarkOakStructures = Arrays.asList("dark_oak/1.structure", "dark_oak/2.structure",
			"dark_oak/3.structure");

	@ConfigOptions(name = "mushroom.brown")
	public List<String> mushroomBrownStructures = Arrays.asList("brown_mushroom/1.schem", "brown_mushroom/2.schem",
			"brown_mushroom/3.schem");

	@ConfigOptions(name = "mushroom.red")
	public List<String> mushroomRedStructures = Arrays.asList("red_mushroom/1.schem", "red_mushroom/2.schem",
			"red_mushroom/3.schem");

	/**
	 * Creates a new plugin config instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */

	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Collections.singletonList("OwnGarden Configuration File"));

		structuresDirectory = new File(dataFolder, "structures/").getPath();
	}

	/**
	 * Returns the structures list which corresponds to the specified material
	 * (sapling / log).
	 *
	 * @param material The material.
	 *
	 * @return The corresponding list.
	 */
	// https://jd.papermc.io/paper/1.21.8/org/bukkit/TreeType.html
	public List<String> getStructures(final TreeType treeType) {
		switch (treeType) {
			// Oak
			case OAK_SAPLING: 
			case OAK_LOG:
				return saplingOakStructures;
			// Spruce
			case SPRUCE_SAPLING: 
			case SPRUCE_LOG:
				return saplingSpruceStructures;
			// Birch
			case BIRCH_SAPLING: 
			case BIRCH_LOG:
				return saplingBirchStructures;
			// Jungle
			case JUNGLE_SAPLING:
			case JUNGLE_LOG:
				return saplingJungleStructures;
			// Acacia
			case ACACIA:
				return saplingAcaciaStructures;
			// Dark Oak
			case DARK_OAK_SAPLING:
			case DARK_OAK_LOG:
				return saplingDarkOakStructures;
			// Red mushroom
			case RED_MUSHROOM:
			case MUSHROOM_STEM:
				return mushroomRedStructures;
			// Brown mushroom
			case BROWN_MUSHROOM:
				return mushroomBrownStructures;
			// Tree with large roots which grows above lush caves
			// ???
			case AZALEA:
				return null; // TODO
			default:
				return null;
		}
	}

}