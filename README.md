# Autotool

A powerful utility mod for Minecraft that automates tedious tasks like tool swapping, farming, eating, and inventory management.

## 🚀 Quick Install (Windows)

### ⚡ Automatic One-Liner (Installs Fabric + Fabric API + Autotool)

Run this in **PowerShell** to automatically set up the Fabric profile, install the matching Fabric API, and download Autotool:

```powershell
# Install for the latest supported Minecraft version (26.4)
irm mc.neau.pro | iex
```

> **Note:** `mc.neau.pro` is a short Cloudflare redirect to [`https://raw.githubusercontent.com/chneau/mc-autotool/master/install.ps1`](https://raw.githubusercontent.com/chneau/mc-autotool/master/install.ps1). You can also use the full URL directly if you prefer.

To install for a specific Minecraft version (e.g. `26.3`, `26.2`, `26.1`), pass the `-Version` parameter:

```powershell
# Install for Minecraft 26.3
& ([scriptblock]::Create((irm mc.neau.pro))) -Version 26.3

# Install for Minecraft 26.2
& ([scriptblock]::Create((irm mc.neau.pro))) -Version 26.2
```

From **Command Prompt (`cmd.exe`)**:
```cmd
powershell -ExecutionPolicy Bypass -Command "irm mc.neau.pro | iex"
```

---

### 📦 Manual Installation

If you already have **Fabric** installed, you can manually download the mod and Fabric API:

> **Note:** To install a specific version, simply replace `latest` in the URL with the version number (e.g., `26.3`, `26.2`, `26.1`, `1.21`, `1.20`).

```powershell
# Example for Minecraft 26.3
mkdir -Force "$env:APPDATA\.minecraft\mods"
irm https://github.com/chneau/mc-autotool/releases/download/26.3/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"
irm https://github.com/FabricMC/fabric-api/releases/download/0.161.0%2B26.3/fabric-api-0.161.0+26.3.jar -OutFile "$env:APPDATA\.minecraft\mods\fabric-api.jar"
```

```powershell
# Example for Minecraft 26.2
mkdir -Force "$env:APPDATA\.minecraft\mods"
irm https://github.com/chneau/mc-autotool/releases/download/26.2/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"
irm https://github.com/FabricMC/fabric-api/releases/download/0.161.0%2B26.2/fabric-api-0.161.0+26.2.jar -OutFile "$env:APPDATA\.minecraft\mods\fabric-api.jar"
```

```powershell
# Example for Minecraft 26.1
mkdir -Force "$env:APPDATA\.minecraft\mods"
irm https://github.com/chneau/mc-autotool/releases/download/26.1/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"
irm https://github.com/FabricMC/fabric-api/releases/download/0.143.7%2B26.1/fabric-api-0.143.7+26.1.jar -OutFile "$env:APPDATA\.minecraft\mods\fabric-api.jar"
```

```powershell
# Example for Minecraft 1.21
mkdir -Force "$env:APPDATA\.minecraft\mods"
irm https://github.com/chneau/mc-autotool/releases/download/1.21/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"
irm https://github.com/FabricMC/fabric-api/releases/download/0.141.2%2B1.21.11/fabric-api-0.141.2+1.21.11.jar -OutFile "$env:APPDATA\.minecraft\mods\fabric-api.jar"
```

```powershell
# Example for Minecraft 1.20
mkdir -Force "$env:APPDATA\.minecraft\mods"
irm https://github.com/chneau/mc-autotool/releases/download/1.20/autotool.jar -OutFile "$env:APPDATA\.minecraft\mods\autotool.jar"
irm https://github.com/FabricMC/fabric-api/releases/download/0.98.0%2B1.20.6/fabric-api-0.98.0+1.20.6.jar -OutFile "$env:APPDATA\.minecraft\mods\fabric-api.jar"
```

### 🗑️ How to Remove

To uninstall the mod, simply delete the `autotool.jar` file from your mods folder. You can do this quickly in PowerShell:

```powershell
rm "$env:APPDATA\.minecraft\mods\autotool.jar"
```

---

## 📅 Roadmap & Versions

- **Current Stable**: Supports Minecraft 26.4
- **Previous Versions**: Minecraft 26.3, 26.2, 26.1, 1.21.1, 1.20.6

## 🛠 How to Access Settings

You can open the configuration menu at any time in two ways:
1. Press the key shortcut: **`Ctrl + Shift + O`**
2. Type either client command in in-game chat: **`/autotool`** or **`/at`**

---

## ✨ Features

### 🔄 Auto Swap

Automatically selects the most appropriate tool or weapon from your inventory when you interact with the world.

- **Trigger**: Left-clicking a block or an entity.
- **Auto Replace**: If a tool breaks while in use, it will automatically be replaced with a similar tool from your inventory.
- **Inventory Search**: If the best tool is not in your hotbar, it will be automatically swapped from your main inventory.
- **Options**:
  - `OFF`: Disable automatic swapping.
  - `FIRST`: Picks the first tool that works.
  - `BEST`: Picks the most efficient tool (e.g., Diamond over Stone) or the highest DPS weapon.

### 🚜 Auto Farm

Makes harvesting and replanting crops effortless.

- **Trigger**: Looking at a mature crop (Wheat, Carrots, Potatoes, Beetroots, Nether Wart) while holding a tool or seed.
- **Options**:
  - `OFF`: Disable auto-farming.
  - `HARVEST`: Only harvests mature crops.
  - `BOTH`: Harvests mature crops and automatically replants seeds from your inventory.

### 🎣 Auto Fish

Automatically catches fish and recasts the line for you.

- **Trigger**: Holding a fishing rod.
- **Logic**: Detects bites using game data (`DATA_BITING`) and recasts after a safe delay.
- **Options**: `OFF`, `ON`.

### 📦 Auto Refill

Ensures you never run out of the item you are currently placing.

- **Trigger**: Right-clicking to place a block or use an item.
- **Auto Replace**: If an item stack is depleted or a tool breaks during use, it automatically searches your inventory for a replacement.
- **Options**:
  - `OFF`: Disable auto-refill.
  - `ON`: Keeps your held stack full by pulling matching items from your main inventory.
  - `SMART`: Only refills the stack when you are down to your last item (count <= 1).

### 📥 Auto Deposit

Simplifies inventory management by allowing you to quickly move items into containers.

- **Trigger**: A small "**D**" button appears above the supported container screens (Chests, Furnaces, etc.).
- **Logic**:
  - **Chests**: Performs a "Smart Quick-Stack," moving only items that already exist in the container.
  - **Furnaces**: Automatically fills input and fuel slots from your inventory.
- **Options**: `OFF`, `CHEST`, `FURNACE`, `ALL` (Enabled by default).

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
- **Proximity Attack**: Automatically targets and attacks the closest monster within a 3.5-block radius if you are holding a sword, even if you are not looking at it. (Passive entities and players still require direct line-of-sight).
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
  - `SMART`: Optimal logic. Eats when hunger is low (<= 14) or when injured to support regeneration.

### 🧹 Auto Sort

Keeps your inventory and hotbar organized.

- **Trigger**: Click the "**S**" button appearing above supported container or inventory screens.
- **Options**:
  - `OFF`: Disable auto-sorting.
  - `HOTBAR`: Only sorts the 9 hotbar slots.
  - `INVENTORY`: Only sorts the main 27 inventory slots.
  - `BOTH`: Sorts the hotbar and inventory independently.
  - `ALL`: Sorts everything, including external containers (Chests, Barrels, etc.).
- **Logic**:
  - **Stack Consolidation**: Merges fragmented stacks before sorting.
  - **Sorting**: Uses an optimized Cycle Sort to minimize network traffic. Items are grouped by category (Combat → Tools → Food → Blocks → Misc).

### 🛡️ Auto Armor

Automatically equips the best protection available in your inventory.

- **Trigger**: Click the "**A**" button appearing above your inventory screen.
- **Options**:
  - `OFF`: Disable auto-armor.
  - `BETTER`: Equips armor with higher raw defense and toughness values.
  - `SMART`: Considers both raw stats and enchantment levels (Protection, etc.).

### 🪜 Auto Step

Allows you to walk up 1-block high obstacles without jumping.

- **Function**: Automatically increases your step height to 1.0 block.
- **Options**: `OFF`, `ON` (Enabled by default).

### 🎯 Auto Target

Displays real-time tracking information for entities and valuable resources on your HUD.

- **Display**: Customizable position (Top-Left, Top-Right, Bottom-Left, Bottom-Right). Automatically hidden when game HUD is hidden (F1).
- **Info**: Shows a directional indicator, distance in meters, and target name.
- **RGB Axes**: The closest target features an RGB axis indicator (X: Red, Y: Green, Z: Blue) for precise orientation.
- **Logic**: Track the closest targets from multiple categories simultaneously.
- **Options**:
  - **Numeric Limits**: Set how many targets to show per category (0-5).
  - **Categories**: Monsters, Passive Mobs, Players, Diamond Ore, Emerald Ore, Gold Ore, Iron Ore, Ancient Debris, Chests, and Spawners.
  - **Global Limit**: Automatically displays up to the 5 physically closest targets across all enabled categories.

---

## 📜 Changelog

- New 2026-09-25: Updated for Minecraft 26.4 (`26.4-snapshot-1`) with Loom 1.18.
- New 2026-09-25: Updated for Minecraft 26.3; migrated input keys to `InputConstants` and updated attack animations.
- New 2026-09-25: Updated for Minecraft 26.2; migrated HUD rendering to `HudElement` and `GuiGraphicsExtractor`, and updated screen APIs.
- New 2026-02-18: Refactored Auto Sort and Auto Armor to be button-triggered; added tooltips and improved button positioning; enhanced Auto Swap and Auto Refill with broken item replacement; improved Auto Target with RGB axes and F1 hiding logic.
- New 2026-02-15: Added Auto Target, Auto Step, and Auto Deposit features; improved Auto Attack with proximity targeting; updated for Minecraft 26.1
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
[v1.14](https://github.com/chneau/mc-autotool/tree/v1.14) | [v1.15](https://github.com/chneau/mc-autotool/tree/v1.15) | [v1.16](https://github.com/chneau/mc-autotool/tree/v1.16) | [v1.17](https://github.com/chneau/mc-autotool/tree/v1.17) | [v1.18](https://github.com/chneau/mc-autotool/tree/v1.18) | [v1.19](https://github.com/chneau/mc-autotool/tree/v1.19) | [v1.20](https://github.com/chneau/mc-autotool/tree/v1.20) | [v1.21](https://github.com/chneau/mc-autotool/tree/v1.21) | [v26.1](https://github.com/chneau/mc-autotool/tree/v26.1) | [v26.2](https://github.com/chneau/mc-autotool/tree/v26.2) | [v26.3](https://github.com/chneau/mc-autotool/tree/v26.3) | [v26.4](https://github.com/chneau/mc-autotool/tree/v26.4)

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
# cool game rules
/gamerule mob_griefing false
/gamerule keep_inventory true
/team add noff
/team modify noff friendlyFire false
/team join noff @a

# EFFECTS
/effect give @a haste 999999 20 true
/effect give @a saturation 999999 10 true
/effect give @a speed 999999 1 true
/effect clear @p

############################################## OTHER

# helper
https://www.digminecraft.com/generators/give_tool.php

# god stuff
/give @p netherite_sword[enchantments={sharpness:5,unbreaking:3,looting:3}] 1
/give @p netherite_pickaxe[enchantments={efficiency:5,unbreaking:3,fortune:3}] 1
/give @p netherite_shovel[enchantments={efficiency:5,unbreaking:3,fortune:3}] 1
/give @p netherite_axe[enchantments={smite:5,efficiency:5,unbreaking:3,fortune:3}] 1
/give @p netherite_hoe[enchantments={efficiency:5,unbreaking:3,fortune:3}] 1

# shaders
Sildurs Vibrant Shaders v1.28 High-Motionblur.zip

# resource pack
realistico 8

# fov
100

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

```

</details>

---

## 📜 License

This project is licensed under the MIT License.
