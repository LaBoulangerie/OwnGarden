package fr.skyost.owngarden.model;

/**
 * Defines which tree generation types a decoration pattern applies to.
 */
public enum DecorationTarget {
    STRUCTURES,
    VANILLA,
    BOTH;

    /**
     * Checks whether this target includes the requested generation type.
     *
     * @param generationType The generation type to check
     * @return {@code true} if decorations should be placed
     */
    public boolean includes(DecorationTarget generationType) {
        return this == BOTH || this == generationType;
    }
}
