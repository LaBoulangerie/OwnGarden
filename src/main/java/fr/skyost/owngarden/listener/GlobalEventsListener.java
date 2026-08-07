package fr.skyost.owngarden.listener;

import fr.skyost.owngarden.OwnGarden;
import fr.skyost.owngarden.config.PluginConfig.HeightCheckFailBehavior;
import fr.skyost.owngarden.model.DecorationPattern;
import fr.skyost.owngarden.util.DecorationPlacer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockTransformer;
import org.bukkit.util.BlockVector;

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
        Biome biome = location.getBlock().getBiome();

        List<Structure> structures = this.plugin.getOwnGardenConfig().getTreeTypeStructures(treeType, biome);

        if (structures.isEmpty()) {
            return;
        }

        Structure randomStructure = structures.get(RANDOM.nextInt(structures.size()));

        // Detect 2x2 trees and find origin
        boolean is2x2 = is2x2Tree(treeType);
        Location origin2x2 = null;
        Material saplingType = location.getBlock().getType();

        if (is2x2) {
            origin2x2 = find2x2Origin(location, saplingType);
            if (origin2x2 == null) {
                return;
            }
        }

        // Height check location: center of 2x2 pattern or sapling location
        // For 2x2, we use origin + (1, 1) which is the SE corner, closest to geometric center
        Location heightCheckLocation = is2x2 ? origin2x2.clone().add(1, 0, 1) : location;
        int checkRadius = is2x2 ? 2 : 1; // 5x5 for 2x2 trees, 3x3 otherwise

        // Vérification de hauteur
        if (this.plugin.getOwnGardenConfig().structuresHeightCheckEnabled) {
            int sizeY = randomStructure.getSize().getBlockY();
            boolean hasSpace = true;

            outerLoop:
            for (int y = 1; y < sizeY; y++) {
                for (int dx = -checkRadius; dx <= checkRadius; dx++) {
                    for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                        Block block = heightCheckLocation.clone().add(dx, y, dz).getBlock();
                        if (!isBlockReplaceable(block.getType())) {
                            hasSpace = false;
                            break outerLoop;
                        }
                    }
                }
            }

            if (!hasSpace) {
                HeightCheckFailBehavior behavior = this.plugin.getOwnGardenConfig().getHeightCheckFailBehavior(treeType);

                if (behavior == HeightCheckFailBehavior.CANCEL) {
                    event.setCancelled(true);
                    event.getBlocks().clear();
                }
                // VANILLA: on ne fait rien, l'arbre vanilla pousse normalement
                return;
            }
        }

        // Clear 2x2 saplings before placing structure
        if (is2x2 && origin2x2 != null) {
            clear2x2Saplings(origin2x2);
        }

        StructureRotation rotation = StructureRotation.NONE;
        Mirror mirror = Mirror.NONE;
        if (this.plugin.getOwnGardenConfig().structuresRandomRotation) {
            rotation = StructureRotation.values()[RANDOM.nextInt(StructureRotation.values().length)];
            mirror = Mirror.values()[RANDOM.nextInt(Mirror.values().length)];
        }

        // Calculer l'offset pour centrer la structure
        Location centerLocation = is2x2 && origin2x2 != null
            ? origin2x2.clone()
            : location.clone();

        BlockVector size = randomStructure.getSize();
        int sizeX = size.getBlockX();
        int sizeZ = size.getBlockZ();

        // Utiliser size/2 pour tous les types - la compensation des transformations est conçue pour cette valeur
        int centerX = sizeX / 2;
        int centerZ = sizeZ / 2;

        Location centeredLocation;

        if (is2x2) {
            // Pour les arbres 2x2 : même logique que 1x1 mais avec le coin NW des 4 blocs centraux
            int offsetX, offsetZ;
            switch (rotation) {
                case CLOCKWISE_90:
                    offsetX = -centerX;
                    offsetZ = centerZ;
                    break;
                case CLOCKWISE_180:
                    offsetX = -centerX;
                    offsetZ = -centerZ;
                    break;
                case COUNTERCLOCKWISE_90:
                    offsetX = centerX;
                    offsetZ = -centerZ;
                    break;
                case NONE:
                default:
                    offsetX = centerX;
                    offsetZ = centerZ;
                    break;
            }

            if (mirror == Mirror.FRONT_BACK) {
                if (rotation == StructureRotation.CLOCKWISE_90 || rotation == StructureRotation.COUNTERCLOCKWISE_90) {
                    offsetZ = -offsetZ;
                } else {
                    offsetX = -offsetX;
                }
            } else if (mirror == Mirror.LEFT_RIGHT) {
                if (rotation == StructureRotation.CLOCKWISE_90 || rotation == StructureRotation.COUNTERCLOCKWISE_90) {
                    offsetX = -offsetX;
                } else {
                    offsetZ = -offsetZ;
                }
            }

            // Correction pour 2x2 : ajuster les offsets positifs de -1
            // car on veut centrer sur les 4 blocs centraux (5,5)-(6,6), pas sur (6,6)
            if (offsetX > 0) offsetX--;
            if (offsetZ > 0) offsetZ--;

            centeredLocation = centerLocation.clone().subtract(offsetX, 0, offsetZ);
        } else {
            // Pour les arbres 1x1 : garder le code existant qui fonctionne
            int offsetX, offsetZ;
            switch (rotation) {
                case CLOCKWISE_90:
                    offsetX = -centerX;
                    offsetZ = centerZ;
                    break;
                case CLOCKWISE_180:
                    offsetX = -centerX;
                    offsetZ = -centerZ;
                    break;
                case COUNTERCLOCKWISE_90:
                    offsetX = centerX;
                    offsetZ = -centerZ;
                    break;
                case NONE:
                default:
                    offsetX = centerX;
                    offsetZ = centerZ;
                    break;
            }

            if (mirror == Mirror.FRONT_BACK) {
                if (rotation == StructureRotation.CLOCKWISE_90 || rotation == StructureRotation.COUNTERCLOCKWISE_90) {
                    offsetZ = -offsetZ;
                } else {
                    offsetX = -offsetX;
                }
            } else if (mirror == Mirror.LEFT_RIGHT) {
                if (rotation == StructureRotation.CLOCKWISE_90 || rotation == StructureRotation.COUNTERCLOCKWISE_90) {
                    offsetX = -offsetX;
                } else {
                    offsetZ = -offsetZ;
                }
            }

            centeredLocation = centerLocation.clone().subtract(offsetX, 0, offsetZ);
        }

        Collection<BlockTransformer> blockTransformers = createBlockTransformers();
        randomStructure.place(centeredLocation, false,
                rotation, mirror, -1, 1, RANDOM,
                blockTransformers, Collections.emptyList());

        // Place decorations if configured
        List<DecorationPattern> decorationPatterns = this.plugin.getOwnGardenConfig()
                .getDecorationPatterns(treeType, biome);
        if (!decorationPatterns.isEmpty()) {
            Location decorationCenter = is2x2 && origin2x2 != null
                    ? origin2x2.clone().add(0.5, 0, 0.5)
                    : location.clone();
            for (DecorationPattern decorationPattern : decorationPatterns) {
                DecorationPlacer.placeDecorations(decorationCenter, decorationPattern, is2x2);
            }
        }

        event.getBlocks().clear();
        event.setCancelled(true);
    }

    /**
     * Creates block transformers that prevent replacing non-replaceable blocks.
     */
    private Collection<BlockTransformer> createBlockTransformers() {
        return List.of(
            (region, x, y, z, current, state) -> {
                org.bukkit.block.BlockState worldBlock = state.getWorld();
                Material existingMaterial = worldBlock.getType();

                if (!isBlockReplaceable(existingMaterial)) {
                    return worldBlock;
                }

                return current;
            }
        );
    }

    /**
     * Checks if a block type can be replaced by a tree.
     * Uses Minecraft's native REPLACEABLE_BY_TREES tag plus logs/leaves/wood.
     */
    private boolean isBlockReplaceable(Material type) {
        return type.isAir()
                || Tag.REPLACEABLE_BY_TREES.isTagged(type)
                || type.name().endsWith("_SAPLING")
                || Tag.LEAVES.isTagged(type);
    }

    /**
     * Checks if the given tree type is a 2x2 tree (requires 4 saplings).
     */
    private boolean is2x2Tree(TreeType treeType) {
        return treeType == TreeType.DARK_OAK
            || treeType == TreeType.MEGA_REDWOOD
            || treeType == TreeType.JUNGLE;
    }

    /**
     * Finds the north-west origin (min X, min Z) of a 2x2 sapling pattern.
     * The triggering sapling can be any of the 4.
     */
    private Location find2x2Origin(Location saplingLoc, Material saplingType) {
        int[][] offsets = {{0, 0}, {-1, 0}, {0, -1}, {-1, -1}};

        for (int[] offset : offsets) {
            Location origin = saplingLoc.clone().add(offset[0], 0, offset[1]);
            if (is2x2Pattern(origin, saplingType)) {
                return origin;
            }
        }
        return null;
    }

    /**
     * Checks if the 4 blocks starting from origin form a 2x2 sapling pattern.
     */
    private boolean is2x2Pattern(Location origin, Material saplingType) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                if (origin.clone().add(dx, 0, dz).getBlock().getType() != saplingType) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Clears the 4 saplings in a 2x2 pattern starting from origin.
     */
    private void clear2x2Saplings(Location origin) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                origin.clone().add(dx, 0, dz).getBlock().setType(Material.AIR);
            }
        }
    }

}