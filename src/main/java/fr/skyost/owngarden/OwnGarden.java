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
            }

            /* LOAD CUSTOM TREE TYPE STRUCTURES : */
            StructureManager structureManager = this.getServer().getStructureManager();
            for (TreeType treeType : TreeType.values()) {
                File treeTypeStructuresDirectory = this.config.getTreeStructuresDirectory(treeType);
                List<Structure> structures = new ArrayList<>();

                for (File structureFile : treeTypeStructuresDirectory.listFiles()) {
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

                this.config.setTreeTypeStructures(treeType, structures);
                this.log(structures.size() + " <gold>custom tree structures loaded for</gold> "
                        + this.config.getTreeTypeName(treeType));
            }

            /* REGISTERING EVENTS : */
            Bukkit.getPluginManager().registerEvents(new GlobalEventsListener(this), this);

            /* REGISTERING COMMANDS : */
            getCommand("owngarden").setExecutor(new OwnGardenCommand(this));

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
}