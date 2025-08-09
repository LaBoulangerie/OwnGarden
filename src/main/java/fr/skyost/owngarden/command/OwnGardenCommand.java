package fr.skyost.owngarden.command;

import com.google.common.base.Joiner;
import fr.skyost.owngarden.OwnGarden;
import fr.skyost.owngarden.config.PluginConfig;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
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
            this.plugin.log(ChatColor.RED, "You do not have the permission to execute this command.", sender);
            return true;
        }

        PluginDescriptionFile description = this.plugin.getDescription();
        sender.sendMessage(ChatColor.GREEN.toString() + description.getName() + " v" + description.getVersion() + ChatColor.GOLD + " by " + Joiner.on(' ').join(description.getAuthors()));
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 2; i++) {
            builder.append("=");
        }

        PluginConfig config = this.plugin.getOwnGardenConfig();

        String line = builder.toString();
        sender.sendMessage(ChatColor.RESET.toString() + line);
        sender.sendMessage(ChatColor.GOLD.toString() + "STRUCTURES : ");
        sender.sendMessage(ChatColor.RESET.toString() + "" + ChatColor.BOLD + "- Oak : " + ChatColor.RESET + Joiner.on(' ').join(config.saplingOakStructures));
        sender.sendMessage(ChatColor.BOLD.toString() + "- Spruce : " + ChatColor.RESET + Joiner.on(' ').join(config.saplingSpruceStructures));
        sender.sendMessage(ChatColor.BOLD.toString() + "- Jungle : " + ChatColor.RESET + Joiner.on(' ').join(config.saplingJungleStructures));
        sender.sendMessage(ChatColor.BOLD.toString() + "- Acacia : " + ChatColor.RESET + Joiner.on(' ').join(config.saplingAcaciaStructures));
        sender.sendMessage(ChatColor.BOLD.toString() + "- Dark Oak : " + ChatColor.RESET + Joiner.on(' ').join(config.saplingDarkOakStructures));
        sender.sendMessage(ChatColor.BOLD.toString() + "- Brown Mushroom : " + ChatColor.RESET + Joiner.on(' ').join(config.mushroomBrownStructures));
        sender.sendMessage(ChatColor.BOLD.toString() + "- Red Mushroom : " + ChatColor.RESET + Joiner.on(' ').join(config.mushroomRedStructures));
        sender.sendMessage(line);
        sender.sendMessage(ChatColor.GOLD.toString() + "PERMISSIONS : ");

        for (Permission permission: description.getPermissions()) {
            if (sender.hasPermission(permission)) {
                sender.sendMessage(ChatColor.GREEN.toString() + "- You have the permission " + ChatColor.BOLD + permission.getName() + ChatColor.RESET + ChatColor.GREEN + ".");
            } else {
                sender.sendMessage(ChatColor.RED.toString() + "- You do not have the permission " + ChatColor.BOLD + permission.getName() + ChatColor.RESET + ChatColor.RED + ".");
            }
        }

        sender.sendMessage(ChatColor.RESET.toString() + line);
        sender.sendMessage(ChatColor.AQUA.toString() + "" + ChatColor.ITALIC + "The above list is scrollable.");
        return true;
    }
}