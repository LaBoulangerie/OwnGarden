package fr.skyost.owngarden;

import com.google.common.base.Joiner;
import fr.skyost.owngarden.command.OwnGardenCommand;
import fr.skyost.owngarden.config.PluginConfig;
import fr.skyost.owngarden.listener.GlobalEventsListener;
import io.papermc.paper.plugin.configuration.PluginMeta;

import org.bukkit.Bukkit;
import org.bukkit.TreeType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The OwnGarden plugin class.
 */
public class OwnGarden extends JavaPlugin {
    /**
     * The plugin config.
     */
    PluginConfig config;

    @Override
    public void onEnable() {
        try {
            /* CONFIGURATION : */
            this.log("<gold>Loading the configuration...</gold>");

            this.config = new PluginConfig(this.getDataFolder());
            this.config.load();
            this.log("<gold>Configuration loaded !</gold>");

            /* CREATE STRUCTURES ROOT DIRECTORY : */
            File structuresDirectory = new File(this.config.structuresDirectory);
            if (!structuresDirectory.exists() || !structuresDirectory.isDirectory()) {
                structuresDirectory.mkdirs();
            }

            /* CREATE TREE TYPE STRUCTURES DIRECTORY : */
            for (TreeType treeType : TreeType.values()) {
                File treeTypeStructuresDirectory = this.config.getTreeStructuresDirectory(treeType);
                if (!treeTypeStructuresDirectory.exists() || !treeTypeStructuresDirectory.isDirectory()) {
                    treeTypeStructuresDirectory.mkdirs();
                }

                // Create default subdirectory
                File defaultDir = new File(treeTypeStructuresDirectory, "default");
                if (!defaultDir.exists()) {
                    defaultDir.mkdirs();
                }

                // Create biome group subdirectories
                for (String biomeGroup : this.config.structuresBiomeGroups.keySet()) {
                    File biomeGroupDir = new File(treeTypeStructuresDirectory, biomeGroup);
                    if (!biomeGroupDir.exists()) {
                        biomeGroupDir.mkdirs();
                    }
                }
            }

            /* LOAD CUSTOM TREE TYPE STRUCTURES : */
            StructureManager structureManager = this.getServer().getStructureManager();
            for (TreeType treeType : TreeType.values()) {
                File treeTypeStructuresDirectory = this.config.getTreeStructuresDirectory(treeType);
                int totalStructures = 0;

                // Load structures from root directory as "default"
                List<Structure> defaultStructures = loadStructuresFromDirectory(structureManager, treeTypeStructuresDirectory);
                if (!defaultStructures.isEmpty()) {
                    this.config.setTreeTypeStructures(treeType, "default", defaultStructures);
                    totalStructures += defaultStructures.size();
                }

                // Load structures from biome group subdirectories
                File[] subdirectories = treeTypeStructuresDirectory.listFiles(File::isDirectory);
                if (subdirectories != null) {
                    for (File biomeGroupDir : subdirectories) {
                        String biomeGroup = biomeGroupDir.getName().toLowerCase();
                        List<Structure> biomeStructures = loadStructuresFromDirectory(structureManager, biomeGroupDir);
                        if (!biomeStructures.isEmpty()) {
                            this.config.setTreeTypeStructures(treeType, biomeGroup, biomeStructures);
                            totalStructures += biomeStructures.size();
                            this.log("<gold>Loaded</gold> " + biomeStructures.size() + " <gold>structures for</gold> "
                                    + this.config.getTreeTypeName(treeType) + "/" + biomeGroup);
                        }
                    }
                }

                if (totalStructures > 0) {
                    this.log(totalStructures + " <gold>total custom tree structures loaded for</gold> "
                            + this.config.getTreeTypeName(treeType));
                }
            }

            /* REGISTERING EVENTS : */
            Bukkit.getPluginManager().registerEvents(new GlobalEventsListener(this), this);

            /* REGISTERING COMMANDS : */
            OwnGardenCommand commandExecutor = new OwnGardenCommand(this);
            getCommand("owngarden").setExecutor(commandExecutor);
            getCommand("owngarden").setTabCompleter(commandExecutor);

            PluginMeta pluginMeta = this.getPluginMeta();
            this.log("<gold>Enabled</gold> <green>" + pluginMeta.getName() + " v" + pluginMeta.getVersion() + "</green> <gold>by</gold> "
                    + Joiner.on("<gold>, </gold>").join(pluginMeta.getAuthors()) + " <gold>!</gold>");
        } catch (Exception e) {
            this.log("<red>Unable to start the plugin !</red>");
            e.printStackTrace();
        }
    }

    /**
     * Logs a message to the console.
     *
     * @param color   The color (after the [plugin-name]).
     * @param message The message.
     * @param sender  The sender.
     */
    public final void log(String message) {
        this.log(message, Bukkit.getConsoleSender());
    }

    /**
     * Logs a message to the console.
     *
     * @param color   The color (after the [plugin-name]).
     * @param message The message.
     * @param sender  The sender.
     */
    public final void log(String message, CommandSender sender) {
        PluginMeta pluginMeta = this.getPluginMeta();
        sender.sendRichMessage("<green>[" + pluginMeta.getName() + "]</green> " + message);
    }

    public final PluginConfig getOwnGardenConfig() {
        return config;
    }

    /**
     * Reloads the plugin configuration and all structures.
     *
     * @return the number of structures loaded, or -1 if reload failed
     */
    public int reloadStructures() {
        // Reload the configuration file
        try {
            config.load();
        } catch (Exception e) {
            this.log("<red>Failed to reload configuration: " + e.getMessage() + "</red>");
            e.printStackTrace();
            return -1;
        }

        // Clear the loaded structures cache
        config.clearLoadedStructures();

        // Reload all structures
        int totalLoadedStructures = 0;
        StructureManager structureManager = this.getServer().getStructureManager();
        for (TreeType treeType : TreeType.values()) {
            File treeTypeStructuresDirectory = this.config.getTreeStructuresDirectory(treeType);
            int totalStructures = 0;

            // Load structures from root directory as "default"
            List<Structure> defaultStructures = loadStructuresFromDirectory(structureManager, treeTypeStructuresDirectory);
            if (!defaultStructures.isEmpty()) {
                this.config.setTreeTypeStructures(treeType, "default", defaultStructures);
                totalStructures += defaultStructures.size();
            }

            // Load structures from biome group subdirectories
            File[] subdirectories = treeTypeStructuresDirectory.listFiles(File::isDirectory);
            if (subdirectories != null) {
                for (File biomeGroupDir : subdirectories) {
                    String biomeGroup = biomeGroupDir.getName().toLowerCase();
                    List<Structure> biomeStructures = loadStructuresFromDirectory(structureManager, biomeGroupDir);
                    if (!biomeStructures.isEmpty()) {
                        this.config.setTreeTypeStructures(treeType, biomeGroup, biomeStructures);
                        totalStructures += biomeStructures.size();
                    }
                }
            }

            if (totalStructures > 0) {
                this.log(totalStructures + " <gold>total custom tree structures loaded for</gold> "
                        + this.config.getTreeTypeName(treeType));
            }
            totalLoadedStructures += totalStructures;
        }
        return totalLoadedStructures;
    }

    /**
     * Loads all structure files from a directory.
     */
    private List<Structure> loadStructuresFromDirectory(StructureManager structureManager, File directory) {
        List<Structure> structures = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null) {
            return structures;
        }

        for (File structureFile : files) {
            if (structureFile.isDirectory()) {
                continue;
            }

            try {
                Structure structure = structureManager.loadStructure(structureFile);
                structures.add(structure);
                this.log("<gold>Custom tree structure loaded</gold> " + structureFile);
            } catch (Exception e) {
                this.log("<red>Unable to load structure</red> " + structureFile);
            }
        }

        return structures;
    }
}