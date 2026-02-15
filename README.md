# Autotool

A powerful utility mod for Minecraft that automates tedious tasks like tool swapping, farming, eating, and inventory management.

## 🚀 Quick Install (Windows)

If you already have **Fabric** installed, you can quickly download the latest version of Autotool (**26.1**) by running this command in PowerShell:

```powershell
# For the latest version (Minecraft 26.1)
irm https://github.com/chneau/mc-autotool/releases/latest/download/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"
```

> **Note:** To install a specific version, simply replace `latest` in the URL with the version number (e.g., `1.21`, `1.20`).

```powershell
# Example for Minecraft 1.21
irm https://github.com/chneau/mc-autotool/releases/download/1.21/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"

# Example for Minecraft 1.20
irm https://github.com/chneau/mc-autotool/releases/download/1.20/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"
```

---

## 📅 Roadmap & Versions

- **Current Stable**: Supports Minecraft 26.1 (Cute Companions)
- **Previous Version**: Minecraft 1.21.1

## 🛠 How to Access Settings

You can open the configuration menu at any time by pressing:
**`Ctrl + Shift + O`**

---

## ✨ Features

### 🔄 Auto Swap

Automatically selects the most appropriate tool or weapon from your hotbar when you interact with the world.

- **Trigger**: Left-clicking a block or an entity.
- **Options**:
  - `OFF`: Disable automatic swapping.
  - `FIRST`: Picks the first tool in your hotbar that works.
  - `BEST`: Picks the most efficient tool (e.g., Diamond over Stone) or the highest DPS weapon.

### 🚜 Auto Farm

Makes harvesting and replanting crops effortless.

- **Trigger**: Looking at a mature crop while holding a tool or seed.
- **Options**:
  - `OFF`: Disable auto-farming.
  - `HARVEST`: Only harvests mature crops.
  - `BOTH`: Harvests mature crops and automatically replants seeds from your inventory.

### 🎣 Auto Fish

Automatically catches fish and recasts the line for you.

- **Trigger**: Holding a fishing rod with an active hook.
- **Logic**: Automatically reels in when a fish bites and recasts after a 2-second delay.
- **Options**: `OFF`, `ON`.

### 📦 Auto Refill

Ensures you never run out of the item you are currently placing.

- **Trigger**: Right-clicking to place a block or use an item.
- **Options**:
  - `OFF`: Disable auto-refill.
  - `ON`: Keeps your held stack full by pulling matching items from your main inventory.
  - `SMART`: Only refills the stack when you are down to your very last item.

### 🏃 Auto Sprint

Maintains your momentum without you having to hold down the sprint key.

- **Trigger**: Moving forward.
- **Options**:
  - `OFF`: Disable auto-sprint.
  - `ON`: Sprints whenever your hunger is high enough.
  - `HUNGER_50`: Only sprints if your hunger bar is above 50%.

### ⚔️ Auto Attack

Automatically attacks entities you are looking at, respecting weapon cool-downs for maximum damage.

- **Trigger**: Looking at a living entity.
- **Options**:
  - `OFF`: Disable auto-attack.
  - `SWORD`: Only auto-attacks when you are holding a sword.
  - `ALL`: Auto-attacks with any item in your hand.

### 🍎 Auto Eat

Keeps you fed and healthy without manual intervention.

- **Trigger**: Automatically starts after **1 second of total inactivity** (no moving, clicking, or jumping).
- **Options**:
  - `OFF`: Disable auto-eating.
  - `HUNGER`: Eats whenever you are missing any hunger points.
  - `HEALTH`: Only eats when you are injured (to maintain natural regeneration).
  - `SMART`: Optimal logic that picks the best food for your current hunger and avoids over-eating.

### 🧹 Auto Sort

Keeps your inventory and hotbar organized automatically.

- **Trigger**: Opening your inventory screen (default key `E`).
- **Options**:
  - `OFF`: Disable auto-sorting.
  - `HOTBAR`: Only sorts the 9 hotbar slots.
  - `INVENTORY`: Only sorts the main 27 inventory slots.
  - `BOTH`: Sorts the hotbar and inventory independently.
- **Logic**: Items are grouped by category: Combat → Tools → Food → Blocks → Misc.

### 🛡️ Auto Armor

Automatically equips the best protection available in your inventory.

- **Trigger**: Continuous check while playing.
- **Options**:
  - `OFF`: Disable auto-armor.
  - `BETTER`: Equips armor with higher raw defense and toughness values.
  - `SMART`: Considers both raw stats and enchantment levels (Protection, etc.).

---

## 📜 Changelog

- New 2026-02-15: Updated for Minecraft 26.1
- New 2026-02-14: Updated for 1.21.1 and added comprehensive Auto features (Eat, Sort, Armor, Fish, etc.)
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

## 🔗 Other Versions

Links to branch-specific code:
[1.14](https://github.com/chneau/mc-autotool/tree/1.14) | [1.15](https://github.com/chneau/mc-autotool/tree/1.15) | [1.16](https://github.com/chneau/mc-autotool/tree/1.16) | [1.17](https://github.com/chneau/mc-autotool/tree/1.17) | [1.18](https://github.com/chneau/mc-autotool/tree/1.18) | [1.19](https://github.com/chneau/mc-autotool/tree/1.19) | [1.20](https://github.com/chneau/mc-autotool/tree/1.20) | [1.21](https://github.com/chneau/mc-autotool/tree/1.21) | [26.1](https://github.com/chneau/mc-autotool/tree/26.1)

## 💡 Inspiration

Inspired by `ControlPack` by uyjulian. <https://github.com/uyjulian/ControlPack>

## 🛠 Development

1. Check [fabricmc.net](https://modmuss50.me/fabric.html) for `gradle.properties` and `fabric.mod.json`.
2. Check [fabric-loom](https://maven.fabricmc.net/fabric-loom/fabric-loom.gradle.plugin/) for `build.gradle`.
3. Run `./gradlew genSources` and verify with `./gradlew build`.

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

---

## 📜 License

This project is licensed under the MIT License.
