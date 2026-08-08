<div align="center">
    <img src="https://media.forgecdn.net/attachments/125/227/logo.png" title="OwnGarden" alt="OwnGarden"/>
</div>

## Overview

OwnGarden replaces default Minecraft tree growth with custom structures. When saplings grow (naturally or via bone meal), the plugin intercepts the growth event and places a randomly selected custom structure instead.

**No external dependencies required** - uses Paper's native `StructureManager` for NBT structure files.

## Features

- **All tree types supported** - Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, and mushrooms
- **2x2 trees** - Full support for Dark Oak, Mega Redwood, and large Jungle trees
- **Biome-based structures** - Different tree variants per biome group (forest, plains, cold, jungle, etc.)
- **Automatic decorations** - Grass, flowers, ferns, and mushrooms around trees based on biome
- **Random rotation & mirroring** - Each tree placement is unique
- **Height check** - Prevents structures from replacing important blocks; configurable fallback behavior
- **Hot reload** - Reload config and structures without server restart

## Requirements

- **Paper 1.21.11+** (or compatible forks)
- **Java 21**

## Installation

1. Download the latest `owngarden.jar` from releases
2. Place it in your server's `plugins/` folder
3. Start/restart the server
4. Add your `.nbt` structure files to `plugins/OwnGarden/structures/`

## Directory Structure

```
plugins/OwnGarden/
├── config.yml
└── structures/
    ├── oak/
    │   ├── default/        # Used when no biome match
    │   ├── forest/         # Forest biomes
    │   └── plains/         # Plains biomes
    ├── birch/
    │   ├── default/
    │   └── ...
    ├── spruce/
    ├── jungle/
    ├── acacia/
    ├── dark_oak/           # 2x2 trees
    ├── mega_redwood/       # 2x2 trees
    └── ...
```

Place `.nbt` structure files in the appropriate folders. The plugin automatically creates all directories on first start.

## Configuration

### Main Settings

| Key | Description | Default |
|-----|-------------|---------|
| `structures.directory` | Root folder for structure files | `plugins/OwnGarden/structures` |
| `structures.random-rotation` | Randomly rotate and mirror structures | `true` |
| `structures.height-check.enabled` | Check for space before placing | `true` |
| `structures.height-check.on-fail.<tree>` | Behavior when space check fails: `CANCEL` (keep sapling) or `VANILLA` (grow default tree) | `CANCEL` |

### Biome Groups

Biomes are grouped for structure and decoration selection:

| Group | Biomes |
|-------|--------|
| `forest` | Forest, Flower Forest, Birch Forest, Dark Forest, Cherry Grove... |
| `plains` | Plains, Sunflower Plains, Meadow |
| `cold` | Taiga, Snowy Taiga, Frozen Peaks, Grove... |
| `jungle` | Jungle, Sparse Jungle, Bamboo Jungle |
| `savanna` | Savanna, Savanna Plateau |
| `swamp` | Swamp, Mangrove Swamp |
| `hot` | Desert, Badlands |

### Decorations

Decorations (grass, flowers, mushrooms) are placed around trees based on biome. Each decoration pattern defines:

- `apply-to` - Generation target: `STRUCTURES`, `VANILLA`, or `BOTH` (defaults to `STRUCTURES`)
- `min-radius` / `max-radius` - Area around the tree
- `replaceable-blocks` - Blocks that can be replaced (AIR, GRASS, etc.)
- `placeable-on-blocks` - Valid ground blocks (GRASS_BLOCK, DIRT, etc.)
- `blocks` - List of blocks to place with their chance and properties

Example pattern:
```yaml
decoration-patterns:
  oak-forest:
    apply-to: BOTH
    min-radius: 2
    max-radius: 4
    blocks:
      - {material: SHORT_GRASS, chance: 0.3}
      - {material: POPPY, chance: 0.04}
      - {material: TALL_GRASS, chance: 0.12, height: 2}
```

Patterns are mapped to tree types per biome:
```yaml
decorations:
  oak:
    default: oak-forest
    plains: oak-plains
```

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/owngarden` | Display plugin info and loaded structures | `owngarden.command` |
| `/owngarden reload` | Reload configuration and structures | `owngarden.reload` |

## Creating Structures

1. Build your tree in-game
2. Use `minecraft:structure_block` to save as NBT (vanilla)
3. Fill air blocks with `minecraft:structure_void`
4. Find the file in `world/generated/minecraft/structures/`
5. Copy to `plugins/OwnGarden/structures/<tree-type>/`

**Tips for structure creation:**
- Center the trunk at the structure's center for proper placement
- For 2x2 trees, the center should be between the 4 trunk blocks
- Leave air around the tree; the plugin won't replace non-replaceable blocks

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `owngarden.command` | op | Use `/owngarden` command |
| `owngarden.reload` | op | Use `/owngarden reload` command |

## Building from Source

```bash
./gradlew build        # Unix/Mac
./gradlew.bat build    # Windows
```

Output: `build/libs/owngarden-<version>.jar`

## Credits

- **Original author**: [Skyost](https://dev.bukkit.org/projects/owngarden)
- **Fork maintainers**: LaBoulangerie, Clem, Red69_Leaf

## Links

- [GitHub Repository](https://github.com/LaBoulangerie/OwnGarden)
- [Report Issues](https://github.com/LaBoulangerie/OwnGarden/issues)
