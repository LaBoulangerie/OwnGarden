package fr.skyost.owngarden.util;

import fr.skyost.owngarden.model.DecorationBlock;
import fr.skyost.owngarden.model.DecorationPattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;

import java.util.List;
import java.util.Random;

/**
 * Utility class for placing decorations around trees.
 */
public class DecorationPlacer {

    private static final Random RANDOM = new Random();
    private static final BlockFace[] CARDINAL_FACES = {
        BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };
    private static final BlockFace[] ALL_FACES = {
        BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
        BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST
    };

    /**
     * Places decorations around a tree according to the given pattern.
     *
     * @param center  The center location (sapling position or 2x2 center)
     * @param pattern The decoration pattern to apply
     * @param is2x2   Whether the tree is a 2x2 tree
     */
    public static void placeDecorations(Location center, DecorationPattern pattern, boolean is2x2) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        int minRadius = pattern.getMinRadius();
        int maxRadius = pattern.getMaxRadius();
        int radius = minRadius + RANDOM.nextInt(maxRadius - minRadius + 1);

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        // Trunk exclusion zone
        int trunkMin = is2x2 ? 0 : 0;
        int trunkMax = is2x2 ? 1 : 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // Skip trunk zone
                if (is2x2) {
                    if (dx >= trunkMin && dx <= trunkMax && dz >= trunkMin && dz <= trunkMax) {
                        continue;
                    }
                } else {
                    if (dx == 0 && dz == 0) {
                        continue;
                    }
                }

                // Check if within circular radius
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > radius) {
                    continue;
                }

                int x = centerX + dx;
                int z = centerZ + dz;

                // Find ground level
                Integer groundY = findGroundLevel(world, x, centerY, z,
                        pattern.getMaxVerticalSearch(), pattern.getPlaceableOnBlocks());
                if (groundY == null) {
                    continue;
                }

                Block groundBlock = world.getBlockAt(x, groundY, z);
                Block aboveBlock = world.getBlockAt(x, groundY + 1, z);

                // Check if above block is replaceable
                if (!isBlockInList(aboveBlock.getType(), pattern.getReplaceableBlocks())) {
                    continue;
                }

                // Try to place a decoration block
                for (DecorationBlock decorationBlock : pattern.getBlocks()) {
                    if (RANDOM.nextDouble() < decorationBlock.getChance()) {
                        // For 2-height blocks, check if the second block is also replaceable
                        if (decorationBlock.getHeight() == 2) {
                            Block secondBlock = world.getBlockAt(x, groundY + 2, z);
                            if (!isBlockInList(secondBlock.getType(), pattern.getReplaceableBlocks())) {
                                continue; // Skip if the 2nd block isn't replaceable
                            }
                        }
                        placeDecorationBlock(aboveBlock, decorationBlock);
                        break; // Only place one decoration per position
                    }
                }
            }
        }
    }

    /**
     * Finds the ground level at the given position.
     *
     * @param world        The world
     * @param x            X coordinate
     * @param startY       Starting Y coordinate
     * @param z            Z coordinate
     * @param maxSearch    Maximum search distance up and down
     * @param placeableOn  List of valid ground block types
     * @return The Y coordinate of the ground block, or null if not found
     */
    private static Integer findGroundLevel(World world, int x, int startY, int z,
                                           int maxSearch, List<String> placeableOn) {
        // Search downward first
        for (int dy = 0; dy >= -maxSearch; dy--) {
            int y = startY + dy;
            Block block = world.getBlockAt(x, y, z);
            Block above = world.getBlockAt(x, y + 1, z);

            if (isBlockInList(block.getType(), placeableOn) && above.getType().isAir()) {
                return y;
            }
        }

        // Search upward
        for (int dy = 1; dy <= maxSearch; dy++) {
            int y = startY + dy;
            Block block = world.getBlockAt(x, y, z);
            Block above = world.getBlockAt(x, y + 1, z);

            if (isBlockInList(block.getType(), placeableOn) && above.getType().isAir()) {
                return y;
            }
        }

        return null;
    }

    /**
     * Places a decoration block at the given location.
     *
     * @param block           The block to modify
     * @param decorationBlock The decoration block configuration
     */
    private static void placeDecorationBlock(Block block, DecorationBlock decorationBlock) {
        String material = decorationBlock.getMaterial();
        BlockData blockData;

        try {
            // Try to parse as full BlockData string (e.g., "minecraft:wildflowers[flower_amount=2]")
            if (material.contains("[") || material.contains(":")) {
                blockData = Bukkit.createBlockData(material);
            } else {
                // Parse as simple material name
                Material mat = Material.matchMaterial(material);
                if (mat == null) {
                    return;
                }
                blockData = mat.createBlockData();
            }
        } catch (IllegalArgumentException e) {
            // Invalid block data
            return;
        }

        // Apply random rotation if requested
        if (decorationBlock.hasRandomRotation()) {
            blockData = applyRandomRotation(blockData);
        }

        // Handle bisected (2-height) blocks
        if (decorationBlock.getHeight() == 2 && blockData instanceof Bisected) {
            // Place lower half
            Bisected lowerData = (Bisected) blockData;
            lowerData.setHalf(Bisected.Half.BOTTOM);
            block.setBlockData(lowerData, false);

            // Place upper half
            Block upperBlock = block.getRelative(BlockFace.UP);
            BlockData upperBlockData;
            try {
                upperBlockData = blockData.clone();
                ((Bisected) upperBlockData).setHalf(Bisected.Half.TOP);
                upperBlock.setBlockData(upperBlockData, false);
            } catch (Exception e) {
                // Fallback: create new block data for upper half
                try {
                    upperBlockData = Bukkit.createBlockData(blockData.getMaterial());
                    ((Bisected) upperBlockData).setHalf(Bisected.Half.TOP);
                    upperBlock.setBlockData(upperBlockData, false);
                } catch (Exception ignored) {
                    // If still fails, just leave the lower half
                }
            }
        } else {
            block.setBlockData(blockData, false);
        }
    }

    /**
     * Applies a random rotation to the block data.
     *
     * @param blockData The block data to rotate
     * @return The rotated block data
     */
    private static BlockData applyRandomRotation(BlockData blockData) {
        if (blockData instanceof Rotatable) {
            Rotatable rotatable = (Rotatable) blockData;
            rotatable.setRotation(ALL_FACES[RANDOM.nextInt(ALL_FACES.length)]);
        } else if (blockData instanceof Directional) {
            Directional directional = (Directional) blockData;
            directional.setFacing(CARDINAL_FACES[RANDOM.nextInt(CARDINAL_FACES.length)]);
        }
        return blockData;
    }

    /**
     * Checks if a material is in the given list of block type names.
     *
     * @param material The material to check
     * @param list     The list of block type names
     * @return True if the material is in the list
     */
    private static boolean isBlockInList(Material material, List<String> list) {
        String name = material.name();
        for (String entry : list) {
            if (entry.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
