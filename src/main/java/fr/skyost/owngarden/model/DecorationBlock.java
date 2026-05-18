package fr.skyost.owngarden.model;

/**
 * Represents a decoration block with its placement properties.
 */
public class DecorationBlock {

    private final String material;
    private final double chance;
    private final boolean randomRotation;
    private final int height;

    /**
     * Creates a new decoration block.
     *
     * @param material       The material name or BlockData string (e.g., "SHORT_GRASS" or "minecraft:wildflowers[flower_amount=2]")
     * @param chance         The probability of placing this block (0.0 to 1.0)
     * @param randomRotation Whether to apply random rotation to directional blocks
     * @param height         The height of the block (1 for normal, 2 for bisected blocks like TALL_GRASS)
     */
    public DecorationBlock(String material, double chance, boolean randomRotation, int height) {
        this.material = material;
        this.chance = chance;
        this.randomRotation = randomRotation;
        this.height = height;
    }

    /**
     * Creates a new decoration block with height 1.
     *
     * @param material       The material name or BlockData string (e.g., "SHORT_GRASS" or "minecraft:wildflowers[flower_amount=2]")
     * @param chance         The probability of placing this block (0.0 to 1.0)
     * @param randomRotation Whether to apply random rotation to directional blocks
     */
    public DecorationBlock(String material, double chance, boolean randomRotation) {
        this(material, chance, randomRotation, 1);
    }

    /**
     * Gets the material name or BlockData string.
     *
     * @return The material
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Gets the placement chance.
     *
     * @return The chance (0.0 to 1.0)
     */
    public double getChance() {
        return chance;
    }

    /**
     * Checks if random rotation should be applied.
     *
     * @return True if random rotation is enabled
     */
    public boolean hasRandomRotation() {
        return randomRotation;
    }

    /**
     * Gets the height of the block.
     *
     * @return The height (1 for normal blocks, 2 for bisected blocks like TALL_GRASS)
     */
    public int getHeight() {
        return height;
    }
}
