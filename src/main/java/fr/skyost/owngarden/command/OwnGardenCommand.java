package fr.skyost.owngarden.command;

import com.google.common.base.Joiner;
import fr.skyost.owngarden.OwnGarden;
import fr.skyost.owngarden.config.PluginConfig;
import io.papermc.paper.plugin.configuration.PluginMeta;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.TreeType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.structure.Structure;
import org.bukkit.util.ChatPaginator;

/**
 * The /owngarden command.
 */
public class OwnGardenCommand implements CommandExecutor, TabCompleter {

    private final OwnGarden plugin;

    public OwnGardenCommand(OwnGarden plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("owngarden.command")) {
            this.plugin.log("<red>You do not have the permission to execute this command.</red>", sender);
            return true;
        }

        // Handle reload subcommand
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("owngarden.reload")) {
                this.plugin.log("<red>You do not have the permission to reload the plugin.</red>", sender);
                return true;
            }
            int structuresLoaded = this.plugin.reloadStructures();
            if (structuresLoaded >= 0) {
                this.plugin.log("<green>Configuration reloaded successfully!</green> <gold>" + structuresLoaded + "</gold> <green>structures loaded.</green>", sender);
            } else {
                this.plugin.log("<red>Failed to reload configuration. Check console for details.</red>", sender);
            }
            return true;
        }

        PluginMeta pluginMeta = this.plugin.getPluginMeta();

        this.plugin.log("<gold>Enabled</gold> <green>" + pluginMeta.getName() + " v" + pluginMeta.getVersion()
                + "</green> <gold>by</gold> "
                + Joiner.on("<gold>, </gold>").join(pluginMeta.getAuthors()) + " <gold>!</gold>", sender);

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 2; i++) {
            builder.append("=");
        }
        String line = builder.toString();
        sender.sendMessage(line);

        PluginConfig config = this.plugin.getOwnGardenConfig();

        this.plugin.log("<gold>STRUCTURES : </gold>", sender);
        for (TreeType treeType : TreeType.values()) {
            List<Structure> structures = config.getTreeTypeStructures(treeType);
            sender.sendRichMessage(
                    " <gold>+</gold> " + structures.size() + " <gold>custom tree structures loaded for</gold> "
                            + config.getTreeTypeName(treeType));
        }

        sender.sendMessage(line);
        this.plugin.log("<gold>DECORATION PATTERNS : </gold>", sender);
        List<String> patternNames = config.getDecorationPatternNames();
        if (patternNames.isEmpty()) {
            sender.sendRichMessage(" <gray>No decoration patterns loaded</gray>");
        } else {
            for (String patternName : patternNames) {
                sender.sendRichMessage(" <gold>+</gold> " + patternName);
            }
        }

        sender.sendMessage(line);
        this.plugin.log("<gold>PERMISSIONS : </gold>", sender);

        for (Permission permission : pluginMeta.getPermissions()) {
            if (sender.hasPermission(permission)) {
                sender.sendRichMessage("<green>- You have the permission <b>" + permission.getName() + "</b>.</green>");
            } else {
                sender.sendRichMessage(
                        "<red>- You do not have the permission <b>" + permission.getName() + "</b>.</red>");
            }
        }

        sender.sendMessage(line);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("owngarden.reload")) {
                completions.add("reload");
            }
        }
        return completions;
    }
}