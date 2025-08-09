package fr.skyost.owngarden;

import com.google.common.base.Joiner;
import fr.skyost.owngarden.command.OwnGardenCommand;
import fr.skyost.owngarden.config.PluginConfig;
import fr.skyost.owngarden.listener.GlobalEventsListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

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
            log(ChatColor.GOLD, "Loading the configuration...");

            PluginConfig pluginConfig = new PluginConfig(this.getDataFolder());
            this.config = pluginConfig;
            this.config.load();
            log(ChatColor.GOLD, "Configuration loaded !");

            /* EXTRACTING DEFAULT STRUCTURES IF NEEDED : */
            File shematicsDirectory = new File(pluginConfig.structuresDirectory);
            if (!shematicsDirectory.exists() || !shematicsDirectory.isDirectory()) {
                shematicsDirectory.mkdirs();
            }

            if (shematicsDirectory.list().length == 0) {
                log(ChatColor.GOLD, "Extracting samples structures...");
                extractSamples(shematicsDirectory);
                log(ChatColor.GOLD, "Done !");
            }

            /* TESTING STRUCTURES : */
            // log(ChatColor.GOLD, "Testing structures...")
            // val invalidStructures = worldEditOperations!!.testStructures()
            // if (invalidStructures.isNotEmpty()) {
            //     log(ChatColor.RED, "There are some invalid structures :")
            //     for (invalidStructure in invalidStructures) {
            //         log(ChatColor.RED, invalidStructure)
            //         pluginConfig.saplingOakStructures.remove(invalidStructure)
            //         pluginConfig.saplingSpruceStructures.remove(invalidStructure)
            //         pluginConfig.saplingBirchStructures.remove(invalidStructure)
            //         pluginConfig.saplingJungleStructures.remove(invalidStructure)
            //         pluginConfig.saplingAcaciaStructures.remove(invalidStructure)
            //         pluginConfig.saplingDarkOakStructures.remove(invalidStructure)
            //         pluginConfig.mushroomBrownStructures.remove(invalidStructure)
            //         pluginConfig.mushroomRedStructures.remove(invalidStructure)
            //     }
            //     log(ChatColor.RED, "They are not going to be used by the plugin. Please fix them and restart your server.")
            // } else {
            //     log(ChatColor.GOLD, "Done, no error.")
            // }

            /* REGISTERING EVENTS : */
            Bukkit.getPluginManager().registerEvents(new GlobalEventsListener(this), this);

            /* REGISTERING COMMANDS : */
            getCommand("owngarden").setExecutor(new OwnGardenCommand(this));

            PluginDescriptionFile description = this.getDescription();
            log(ChatColor.RESET, "Enabled " + ChatColor.GREEN + description.getName() + " v" + description.getVersion() + ChatColor.GOLD + " by " + Joiner.on(' ').join(description.getAuthors()) + ChatColor.RESET + " !");
        } catch (Exception e) {
            log(ChatColor.RED, "Unable to start the plugin !");
            e.printStackTrace();
        }
    }

    /**
     * Extracts the samples to the specified directory.
     *
     * @param structuresDirectory The structures directory.
     */
    private final void extractSamples(File structuresDirectory) {
        // ZipUtil.unpack(file, structuresDirectory) { name: String -> if (name.startsWith("structures/")) name.replaceFirst("structures/".toRegex(), "") else null }
    }

    /**
     * Logs a message to the console.
     *
     * @param color The color (after the [plugin-name]).
     * @param message The message.
     * @param sender The sender.
     */
    public final void log(ChatColor color, String message) {
        Bukkit.getConsoleSender().sendMessage("[" + this.getDescription().getName() + "] " + color + message);
    }

    /**
     * Logs a message to the console.
     *
     * @param color The color (after the [plugin-name]).
     * @param message The message.
     * @param sender The sender.
     */
    public final void log(ChatColor color, String message, CommandSender sender) {
        sender.sendMessage("[" + this.getDescription().getName() + "] " + color + message);
    }

    public final PluginConfig getOwnGardenConfig() {
        return config;
    }
}