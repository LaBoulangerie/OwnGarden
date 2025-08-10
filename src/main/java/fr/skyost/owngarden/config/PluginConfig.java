package fr.skyost.owngarden.config;

import fr.skyost.owngarden.util.Skyoconfig;

import org.bukkit.TreeType;
import org.bukkit.structure.Structure;

import java.io.File;
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

	@ConfigOptions(name = "structures.check-height")
	public boolean structuresCheckHeight = false;

	private HashMap<TreeType, List<Structure>> loadedTreeTypeStructures;

	/**
	 * Creates a new plugin config instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */

	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Collections.singletonList("OwnGarden Configuration File"));

		structuresDirectory = new File(dataFolder, "structures/").getPath();
		this.loadedTreeTypeStructures = new HashMap<>();
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

	public final void setTreeTypeStructures(final TreeType treeType, final List<Structure> structures) {
		this.loadedTreeTypeStructures.put(treeType, structures);
	}

	public final List<Structure> getTreeTypeStructures(TreeType treeType) {
		return this.loadedTreeTypeStructures.get(treeType);
	}
	
}