package fr.skyost.owngarden.listener;

import fr.skyost.owngarden.OwnGarden;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

/**
 * Global events handled by the plugin.
 */
public class GlobalEventsListener implements Listener {

    private final OwnGarden plugin;

    public GlobalEventsListener(OwnGarden plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the plugin instance.
     *
     * @return The plugin instance.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    final void onStructureGrow(StructureGrowEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Location location = event.getLocation();
        TreeType treeType = event.getSpecies();

        this.plugin.log(ChatColor.WHITE, location + " " + treeType);
        
        // val structures = plugin.pluginConfig!!.getStructures(location.block.type)
        // if (plugin.worldEditOperations!!.growTree(structures, location)) {
        // if (structures === plugin.pluginConfig!!.saplingDarkOakStructures) {
        // val current = location.block
        // for (blockFace in FACES) {
        // val relative = current.getRelative(blockFace)
        // if (relative.type == Material.DARK_OAK_SAPLING) {
        // relative.type = Material.AIR
        // }
        // }
        // }

        // //event.getBlocks().clear();
        // event.setCancelled(true);
    }

    // companion object {
    // private val FACES = listOf(
    // BlockFace.NORTH,
    // BlockFace.NORTH_EAST,
    // BlockFace.EAST,
    // BlockFace.SOUTH_EAST,
    // BlockFace.SOUTH,
    // BlockFace.SOUTH_WEST,
    // BlockFace.WEST,
    // BlockFace.NORTH_WEST
    // )
}