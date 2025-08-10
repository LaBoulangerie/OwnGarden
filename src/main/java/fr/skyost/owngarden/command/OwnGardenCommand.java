package fr.skyost.owngarden.command;

import com.google.common.base.Joiner;
import fr.skyost.owngarden.OwnGarden;
import fr.skyost.owngarden.config.PluginConfig;
import io.papermc.paper.plugin.configuration.PluginMeta;

import java.util.List;

import org.bukkit.TreeType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.structure.Structure;
import org.bukkit.util.ChatPaginator;

/**
 * The /owngarden command.
 */
public class OwnGardenCommand implements CommandExecutor {

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
}