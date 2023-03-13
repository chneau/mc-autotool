# mc-autotool

[![Build Status](https://travis-ci.org/chneau/mc-autotool.svg?branch=master)](https://travis-ci.org/chneau/mc-autotool)

This mod takes the first good tool for the thing you're trying to mine.  
Put your best tools on your hotbar and just forget about it.

## What does this mod do?

- Mining? It takes your pickaxe.
- Cutting woods? It takes your axe.
- Digging? It takes your shovel.
- Attacking? It takes your sword.
  - Having your sword in hand? It will attack efficiently if you target an entity in attack range.
- Farming? Do it 9x faster, do it right.
  - Having a seed in your hand? It will plant the seed in large area and harvest only mature crops.

Once finished, it will go back to the last item you were holding.  
(except for the sword, so that you are in attack mode)

## Changelog

- New 2023-03-13: Updated for 1.19.3
- New 2021-11-24: Updated for 1.18
- New 2021-06-26: Updated for 1.17
- New 2020-10-01: Updated for 1.16.3
- New 2020-09-02: Updated for 1.16.2
- New 2020-06-29: Updated for 1.16
- New 2020-04-30: Auto harvest with tools. If using a tool with fortune you get more harvest: [reddit](https://www.reddit.com/r/Minecraft/comments/27mkw2/til_fortune_tools_give_you_better_harvests/)
- New 2019-07-25: Auto harvesting/planting IF seeds on hand (still need some tweaks) AND IF looking at fully grown "plant" or farmland block.
- New 2019-07-25: Auto attack entity if you have the sword pulled out and you can touch the entity.
- New 2019-07-25: Now it always choose the best tool.

## Installation

- [Fabric](https://fabricmc.net/use/)  
  Be sure to have it (it creates a new profile on your minecraft launcher).
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files)  
  Download a version appropriate for your minecraft version.  
  Put the `jar` file on `%appdata%\.minecraft\mods\`.
- [This mod](https://github.com/chneau/mc-autotool/releases)  
  Again, download a version appropriate for your minecraft version.  
  Note: `autotool-mcv1.14.jar` is equivalent to `autotool.jar`  
  Put the `jar` file on `%appdata%\.minecraft\mods\`.

## Details on the logic of the mod

It will take the best tool that is capable of breaking the block you are clicking at.  
Example:  
On your hand is currently some bread you found on a strange chest.  
You are looking at a tree and you have a wooden axe and a iron axe, the tool will take the best tool, the wooden axe, once you click on the tree. When you release the click (either finished or don't want to cut some trees after all), you will get back the last thing on your hand, the mysterious piece of bread.

The behaviour is different for entities, if you look at a pig and want to kill it, once you click on it, you will take a sword from your hotbar, but it won't go back to the last item you were holding.  
(this is something I need to figure out)  
Then, the tool will use the sword (generally, but if you don't have a sword possibly it will take an axe).  
If it is a sword, it will attack the entity every 625ms (the time the sword takes to fully "charge").

This mod definitly won't suit everyone.  
There is no configuration and won't have any configuration.

Please take a look at the source code, it's only 387 LOC (spread on about 8 files).

## Other

The code is on the other branches of this repo.  
[1.14](https://github.com/chneau/mc-autotool/tree/1.14)  
[1.15](https://github.com/chneau/mc-autotool/tree/1.15)  
[1.16](https://github.com/chneau/mc-autotool/tree/1.16)  
[1.17](https://github.com/chneau/mc-autotool/tree/1.17)  
[1.18](https://github.com/chneau/mc-autotool/tree/1.18)  
[1.19](https://github.com/chneau/mc-autotool/tree/1.19)

## Inspiration

Thanks to the author of the `ControlPack`, it is a mod I discovered a long time ago (arround mc 1.9).  
So far the best-mod-ever. True quality of life for Minecraft gameplay. <https://github.com/uyjulian/ControlPack>

## Steps to update deps

- https://modmuss50.me/fabric.html for `gradle.properties` and `fabric.mod.json`
- https://maven.fabricmc.net/fabric-loom/fabric-loom.gradle.plugin/ for `build.gradle`
- run `make genSources` and then if there is no errors test with `make test`

## debugging vscode sources and other

```bash
rm -rf .gradle bin build .project .classpath
./gradlew genSources
```

## List of prefered mods

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

# cool game rules to play with bebe
# see more here https://minecraft.gamepedia.com/Commands/gamerule
/gamerule mobGriefing false
/gamerule keepInventory true
/team add noff
/team modify noff friendlyFire false
/team join noff @a

# god stuff
# SWORD + TOOLS
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

# best used with elytra
/give @p minecraft:firework_rocket 128



/give @p netherite_boots{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:depth_strider,lvl:9},{id:feather_falling,lvl:9},{id:fire_protection,lvl:9},{id:frost_walker,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}

# OTHER
/give @p bow{Unbreakable:1,Enchantments:[{id:infinity,lvl:9},{id:flame,lvl:9},{id:punch,lvl:9},{id:power,lvl:9},{id:looting,lvl:9},{id:multishot,lvl:10},{id:piercing,lvl:10},{id:quick_charge,lvl:10}]}
/give @p crossbow{Unbreakable:1,Enchantments:[{id:infinity,lvl:9},{id:flame,lvl:9},{id:punch,lvl:9},{id:power,lvl:9},{id:looting,lvl:9},{id:multishot,lvl:10},{id:piercing,lvl:10},{id:quick_charge,lvl:5}]}
/give @p fishing_rod{Unbreakable:1,Enchantments:[{id:lure,lvl:9},{id:luck_of_the_sea,lvl:9},{id:vanishing_curse,lvl:9}]}
/give @p spectral_arrow 64
/give @p enchanted_golden_apple 64

/give @p netherite_chestplate{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}


# EFFECTS
/effect give @a haste 999999 255 true
/effect give @a luck 999999 255 true
/effect give @p night_vision 999999 255 true

/effect give @p haste 999999 255 true
/effect give @p luck 999999 255 true
/effect give @p regeneration 999999 255 true

/effect give @p invisibility 999999 255 true
/effect give @p water_breathing 999999 255 true
/effect give @p strength 999999 255 true
/effect give @p instant_health 999999 255 true
/effect give @p absorption 999999 255 true
/effect give @p conduit_power 999999 255 true
/effect give @p dolphins_grace 999999 255 true
/effect give @p health_boost 999999 255 true
/effect give @p speed 999999 255 true
/effect clear @p
```

## Dropped ideas

- [ ] TODO: Auto refill of coal and easy refill of chest if item is in inventory and the chest

```bash
# this idea looks like it is just not easily possible.
```
