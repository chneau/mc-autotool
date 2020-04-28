# mc-autotool

[![Build Status](https://travis-ci.org/chneau/mc-autotool.svg?branch=master)](https://travis-ci.org/chneau/mc-autotool)

This mod takes the first good tool for the thing you're trying to mine.  
Put your best tools on your hotbar and just forget about it.

## How it works

New 2019/07/25: Auto harvesting/planting IF seeds on hand (still need some tweaks) AND IF looking at fully grown "plant" or farmland block.  
New 2019/07/25: Auto attack entity if you have the sword pulled out and you can touch the entity.  
New 2019/07/25: Now it always choose the best tool.

Mining? It takes your pickaxe.  
Cutting woods? It takes your axe.  
Digging? It takes your shovel.  
Attacking? It takes your sword.

Once finished, it will go back to the last item you were holding.  
(except for the sword... need to think of a workaround for that)

## Installation

- [Fabric](https://fabricmc.net/use/)  
  Be sure to have it (it creates a new profile on your minecraft launcher).
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files)  
  Download a version appropriate for your minecraft version.  
  Put the `jar` file on `%appdata%\.minecraft\mods\`.
- [This mod](https://github.com/chneau/mc-autotool/releases)  
  Again, download a version appropriate for your minecraft version.  
  Note: `autotool-mcv1.14.jar` is equivalent to `autotool.jar` if it is on the same mc version.  
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

Please take a look at the source code, it's only arround 200 LOC (spread on about 4 files).

## Other

The travis build is there <https://travis-ci.org/chneau/mc-autotool/builds>.  
The code is on the other branches of this repo.  
[1.14](https://github.com/chneau/mc-autotool/tree/1.14)  
[1.15](https://github.com/chneau/mc-autotool/tree/1.15)  

## Inspiration

Thanks to the author of the `ControlPack`, it is a mod I discovered a long time ago (arround mc 1.9).  
So far the best-mod-ever. True quality of life for Minecraft gameplay. <https://github.com/uyjulian/ControlPack>

## Super useful:

<https://modmuss50.me/fabric.html>

## debugging vscode sources and other

```bash
rm -rf .gradle bin build .project .classpath
./gradlew genSources
```

## List of prefered mods

```bash
# mods 1.14
autotool-mcv1.14.4.jar
fabric-api-0.4.0+build.240-1.14.jar
fabricmod_VoxelMap-1.9.13_for_1.14.4.jar
foamfix-0.12.1.jar
modmenu-1.7.11+build.121.jar
modnametooltip_1.14.3-1.12.1.jar
mousewheelie-1.3.5+1.14.4.jar
optifabric-0.5.2.jar
OptiFine_1.14.4_HD_U_F3.jar

# mods 1.15
appleskin-mc1.15-fabric-1.0.8.jar
autotool-mcv1.15.2.jar
fabric-api-0.5.1+build.294-1.15.jar
fabricmod_VoxelMap-1.9.16_for_1.15.2.jar
modmenu-1.10.2+build.32.jar
mousewheelie-1.4.5+mc1.15.2-pre1.jar
optifabric-1.0.0-beta8.jar
preview_OptiFine_1.15.2_HD_U_G1_pre13.jar
durabilityviewer-1.15.2-fabric0.4.23-1.7.jar

# shaders
Sildurs Vibrant Shaders v1.262 High.zip

# resource pack
realistico 8

# fov
quake pro

# cool game rules to play with bebe
# see more here https://minecraft.gamepedia.com/Commands/gamerule
/gamerule mobGriefing false
/gamerule keepInventory true
/team add noff
/team modify noff friendlyFire false
/team join noff @a

# god stuff
# SWORD + TOOLS
/give @p diamond_sword{Unbreakable:1,Enchantments:[{id:sharpness,lvl:9},{id:fire_aspect,lvl:9},{id:looting,lvl:9},{id:sweeping,lvl:9}]}
/give @p diamond_sword{Unbreakable:1,Enchantments:[{id:sharpness,lvl:9999},{id:fire_aspect,lvl:9},{id:looting,lvl:9},{id:sweeping,lvl:9}]}
/give @p diamond_pickaxe{Unbreakable:1,Enchantments:[{id:efficiency,lvl:9},{id:fortune,lvl:9}]}
/give @p diamond_shovel{Unbreakable:1,Enchantments:[{id:efficiency,lvl:5},{id:fortune,lvl:9}]}
/give @p diamond_axe{Unbreakable:1,Enchantments:[{id:efficiency,lvl:9},{id:fortune,lvl:9}]}

# ARMOR WITH MODIFIED ELYTRA
/give @p diamond_helmet{Unbreakable:1,Enchantments:[{id:aqua_affinity,lvl:9},{id:blast_protection,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:respiration,lvl:9},{id:thorns,lvl:9}]}
/give @p elytra{Unbreakable:1,AttributeModifiers:[{AttributeName:"generic.armor",Amount:8,UUIDLeast:1,UUIDMost:1,Slot:"chest"}],Enchantments:[{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}
/give @p diamond_boots{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:depth_strider,lvl:9},{id:feather_falling,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}
/give @p diamond_leggings{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}
/give @p trident{Unbreakable:1,Enchantments:[{id:channeling,lvl:9},{id:impaling,lvl:9},{id:loyalty,lvl:9},{id:riptide,lvl:9},{id:sharpness,lvl:9},{id:looting,lvl:9}]}

/give @p diamond_boots{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:depth_strider,lvl:9},{id:feather_falling,lvl:9},{id:fire_protection,lvl:9},{id:frost_walker,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}

# OTHER
/give @p bow{Unbreakable:1,Enchantments:[{id:infinity,lvl:9},{id:flame,lvl:9},{id:punch,lvl:9},{id:power,lvl:9},{id:looting,lvl:9},{id:multishot,lvl:10},{id:piercing,lvl:10},{id:quick_charge,lvl:10}]}
/give @p crossbow{Unbreakable:1,Enchantments:[{id:infinity,lvl:9},{id:flame,lvl:9},{id:punch,lvl:9},{id:power,lvl:9},{id:looting,lvl:9},{id:multishot,lvl:10},{id:piercing,lvl:10},{id:quick_charge,lvl:5}]}
/give @p fishing_rod{Unbreakable:1,Enchantments:[{id:lure,lvl:9},{id:luck_of_the_sea,lvl:9},{id:vanishing_curse,lvl:9}]}
/give @p spectral_arrow 64
/give @p enchanted_golden_apple 64

/give @p diamond_chestplate{Unbreakable:1,Enchantments:[{id:blast_protection,lvl:9},{id:fire_protection,lvl:9},{id:projectile_protection,lvl:9},{id:protection,lvl:9},{id:thorns,lvl:9}]}


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
## take a look at 

```java
MinecraftClient.itemUseCooldown
MinecraftClient.attackCooldown
```
