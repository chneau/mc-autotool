# mc-autotool

This mod takes the first good tool for the thing you're trying to mine.  
Put your best tools on your hotbar and just forget about it.

## Features

- **Smart Tool Selection (Autotool):** Automatically switches to the most effective tool in your hotbar for the block you are currently mining.
- **Dynamic Combat (Autotool):** Instantly selects your best weapon when engaging an entity.
- **Auto-Revert:** Automatically returns to your previously held item once you stop mining or attacking.
- **Efficient Farming (Autofarm):** Automatically harvests mature crops and replants them in a 3x3 area around your target when holding seeds or a tool.
- **Auto-Attack:** Automatically performs timed attacks (every 625ms) while holding a sword and looking at an entity, ensuring maximum damage efficiency.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/).
2. Download the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) for your Minecraft version and place it in your `mods` folder.
3. Download the [latest release](https://github.com/chneau/mc-autotool/releases) of this mod and place it in your `mods` folder.

## How it works

The mod identifies the best tool in your hotbar for the block you are interacting with. For example, if you have bread in your hand and click a tree, it will switch to your best axe. Releasing the click returns the bread to your hand.

For farming, looking at a mature crop and clicking will harvest it and its neighbors (3x3 area), and automatically replant if you are holding seeds.

For entities, it switches to a sword and performs timed attacks every 625ms (full charge) to maximize damage per second.

## Changelog

- New 2026-02-14: Updated for 1.21
- New 2023-10-20: Updated for 1.20
- New 2023-03-13: Updated for 1.19.3
- New 2021-11-24: Updated for 1.18
- New 2021-06-26: Updated for 1.17
- New 2020-10-01: Updated for 1.16.3
- New 2020-09-02: Updated for 1.16.2
- New 2020-06-29: Updated for 1.16
- New 2020-04-30: Auto harvest with tools. [Fortune tools](https://www.reddit.com/r/Minecraft/comments/27mkw2/til_fortune_tools_give_you_better_harvests/) provide better yields.
- New 2019-07-25: Auto harvesting/planting when seeds are held and looking at mature crops.
- New 2019-07-25: Auto attack entity with sword.
- New 2019-07-25: Best tool selection logic improved.

## Other Versions

Links to branch-specific code:
[1.14](https://github.com/chneau/mc-autotool/tree/1.14) | [1.15](https://github.com/chneau/mc-autotool/tree/1.15) | [1.16](https://github.com/chneau/mc-autotool/tree/1.16) | [1.17](https://github.com/chneau/mc-autotool/tree/1.17) | [1.18](https://github.com/chneau/mc-autotool/tree/1.18) | [1.19](https://github.com/chneau/mc-autotool/tree/1.19) | [1.20](https://github.com/chneau/mc-autotool/tree/1.20) | [1.21](https://github.com/chneau/mc-autotool/tree/1.21)

## Inspiration

Inspired by `ControlPack` by uyjulian. <https://github.com/uyjulian/ControlPack>

## Development

1. Check [fabricmc.net](https://modmuss50.me/fabric.html) for `gradle.properties` and `fabric.mod.json`.
2. Check [fabric-loom](https://maven.fabricmc.net/fabric-loom/fabric-loom.gradle.plugin/) for `build.gradle`.
3. Run `make genSources` and verify with `make test`.

<details>
<summary><b>Debugging & Technical Details</b></summary>

### VS Code Debugging

```bash
rm -rf .gradle bin build .project .classpath
./gradlew genSources
```

### Preferred Mods & Commands (1.16)

```bash
# mods 1.16
appleskin-mc1.16-fabric-1.0.11.jar
autotool-mcv1.16.jar
durabilityviewer-1.16.2-fabric0.17.2-1.8.6.jar
fabric-api-0.19.0+build.398-1.16.jar
fabricmod_VoxelMap-1.10.10_for_1.16.2.jar
modmenu-1.14.6+build.31.jar
modnametooltip_1.16.2-1.15.0.jar
mousewheelie-1.5.3+mc1.16.2-pre1.jar
optifabric-1.4.3.jar
OptiFine_1.16.2_HD_U_G3.jar

# shaders
Sildurs Vibrant Shaders v1.28 High-Motionblur.zip

# resource pack
realistico 8

# fov
100

# cool game rules
/gamerule mobGriefing false
/gamerule keepInventory true
/team add noff
/team modify noff friendlyFire false
/team join noff @a

# god stuff
/give @p netherite_sword{Unbreakable:1,Enchantments:[{id:sharpness,lvl:9},{id:fire_aspect,lvl:9},{id:looting,lvl:9},{id:sweeping,lvl:9}]}
/give @p netherite_sword{Unbreakable:1,Enchantments:[{id:sharpness,lvl:9999},{id:fire_aspect,lvl:9},{id:looting,lvl:9},{id:sweeping,lvl:9}]}
/give @p netherite_pickaxe{Unbreakable:1,Enchantments:[{id:efficiency,lvl:9},{id:fortune,lvl:9}]}
/give @p netherite_shovel{Unbreakable:1,Enchantments:[{id:efficiency,lvl:5},{id:fortune,lvl:9}]}
/give @p netherite_axe{Unbreakable:1,Enchantments:[{id:efficiency,lvl:9},{id:fortune,lvl:9}]}
/give @p netherite_hoe{Unbreakable:1,Enchantments:[{id:efficiency,lvl:9},{id:fortune,lvl:9}]}

# ARMOR WITH MODIFIED ELYTRA
/give @p netherite_helmet{Unbreakable:1,Enchantments:[{id:aqua_affinity,lvl:9},{id:blast_protection,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:respiration,lvl:9},{id:thorns,lvl:9}]}
/give @p elytra{Unbreakable:1,AttributeModifiers:[{AttributeName:"generic.armor",Amount:12,UUIDLeast:1,UUIDMost:1,Slot:"chest"}],Enchantments:[{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}
/give @p netherite_boots{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:depth_strider,lvl:9},{id:feather_falling,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}
/give @p netherite_leggings{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}
/give @p trident{Unbreakable:1,Enchantments:[{id:channeling,lvl:9},{id:impaling,lvl:9},{id:loyalty,lvl:9},{id:riptide,lvl:9},{id:sharpness,lvl:9},{id:looting,lvl:9}]}

# OTHER
/give @p bow{Unbreakable:1,Enchantments:[{id:infinity,lvl:9},{id:flame,lvl:9},{id:punch,lvl:9},{id:power,lvl:9},{id:looting,lvl:9},{id:multishot,lvl:10},{id:piercing,lvl:10},{id:quick_charge,lvl:10}]}
/give @p spectral_arrow 64
/give @p enchanted_golden_apple 64

# EFFECTS
/effect give @a haste 999999 255 true
/effect give @p night_vision 999999 255 true
/effect clear @p
```

</details>

## Dropped ideas

- [ ] Auto refill of coal and easy refill of chest if item is in inventory and the chest (Not easily possible).
