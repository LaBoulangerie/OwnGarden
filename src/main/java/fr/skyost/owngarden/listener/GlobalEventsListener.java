package fr.skyost.owngarden.listener;

import fr.skyost.owngarden.OwnGarden;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.structure.Structure;

/**
 * Global events handled by the plugin.
 */
public class GlobalEventsListener implements Listener {

    private static final Random RANDOM = new Random();

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

        this.plugin.log(location + " " + treeType);
        List<Structure> structures = this.plugin.getOwnGardenConfig().getTreeTypeStructures(treeType);
        this.plugin.log(location + " " + treeType + " " + structures.size() + " structures found");

        if (structures.isEmpty()) {
            this.plugin.log(location + " " + treeType + " nothing custom here");
            return;
        }

        Structure randomStructure = structures.get(RANDOM.nextInt(structures.size()));

        StructureRotation rotation = StructureRotation.NONE;
        Mirror mirror = Mirror.NONE;
        if (this.plugin.getOwnGardenConfig().structuresRandomRotation) {
            rotation = StructureRotation.values()[RANDOM.nextInt(StructureRotation.values().length)];
            mirror = Mirror.values()[RANDOM.nextInt(Mirror.values().length)];
        }

        randomStructure.place(location, false,
                rotation, mirror, -1, 1, RANDOM);

        event.getBlocks().clear();
        event.setCancelled(true);
    }

}