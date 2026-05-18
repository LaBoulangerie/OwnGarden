package fr.skyost.owngarden.model;

import java.util.List;

/**
 * Represents a decoration pattern template with placement rules.
 */
public class DecorationPattern {

    private final String name;
    private final int minRadius;
    private final int maxRadius;
    private final int maxVerticalSearch;
    private final List<DecorationBlock> blocks;
    private final List<String> replaceableBlocks;
    private final List<String> placeableOnBlocks;

    /**
     * Creates a new decoration pattern.
     *
     * @param name              The pattern identifier
     * @param minRadius         Minimum radius around the tree
     * @param maxRadius         Maximum radius around the tree
     * @param maxVerticalSearch Maximum vertical search distance for ground level
     * @param blocks            List of decoration blocks to place
     * @param replaceableBlocks List of block types that can be replaced
     * @param placeableOnBlocks List of block types decorations can be placed on
     */
    public DecorationPattern(String name, int minRadius, int maxRadius, int maxVerticalSearch,
                             List<DecorationBlock> blocks, List<String> replaceableBlocks,
                             List<String> placeableOnBlocks) {
        this.name = name;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.maxVerticalSearch = maxVerticalSearch;
        this.blocks = blocks;
        this.replaceableBlocks = replaceableBlocks;
        this.placeableOnBlocks = placeableOnBlocks;
    }

    /**
     * Gets the pattern name.
     *
     * @return The pattern identifier
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the minimum radius.
     *
     * @return The minimum radius
     */
    public int getMinRadius() {
        return minRadius;
    }

    /**
     * Gets the maximum radius.
     *
     * @return The maximum radius
     */
    public int getMaxRadius() {
        return maxRadius;
    }

    /**
     * Gets the maximum vertical search distance.
     *
     * @return The max vertical search
     */
    public int getMaxVerticalSearch() {
        return maxVerticalSearch;
    }

    /**
     * Gets the decoration blocks.
     *
     * @return The list of decoration blocks
     */
    public List<DecorationBlock> getBlocks() {
        return blocks;
    }

    /**
     * Gets the replaceable block types.
     *
     * @return The list of replaceable block type names
     */
    public List<String> getReplaceableBlocks() {
        return replaceableBlocks;
    }

    /**
     * Gets the block types decorations can be placed on.
     *
     * @return The list of placeable-on block type names
     */
    public List<String> getPlaceableOnBlocks() {
        return placeableOnBlocks;
    }
}
