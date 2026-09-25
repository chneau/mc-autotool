# Autotool

A powerful utility mod for Minecraft Fabric that automates tedious tasks like tool swapping, farming, eating, and inventory management.

---

## 🚀 Quick Install (Windows)

### ⚡ Automatic One-Liner (Installs Fabric + Fabric API + Autotool)

Run this in **PowerShell** to automatically set up the Fabric profile, install the matching Fabric API, and download Autotool:

```powershell
# Install for the latest supported Minecraft version (26.4)
irm mc.neau.pro | iex
```

> **Note:** `mc.neau.pro` is a short Cloudflare redirect to [`https://raw.githubusercontent.com/chneau/mc-autotool/master/install.ps1`](https://raw.githubusercontent.com/chneau/mc-autotool/master/install.ps1).

To install for a specific supported Minecraft version (e.g. `26.3`, `26.2`, `26.1`), pass the `-Version` parameter:

```powershell
# Install for Minecraft 26.3
& ([scriptblock]::Create((irm mc.neau.pro))) -Version 26.3

# Install for Minecraft 26.2
& ([scriptblock]::Create((irm mc.neau.pro))) -Version 26.2

# Install for Minecraft 26.1
& ([scriptblock]::Create((irm mc.neau.pro))) -Version 26.1
```

From **Command Prompt (`cmd.exe`)**:
```cmd
powershell -ExecutionPolicy Bypass -Command "irm mc.neau.pro | iex"
```

### 🗑️ How to Remove

To uninstall the mod, delete `autotool.jar` from your mods folder:

```powershell
rm "$env:APPDATA\.minecraft\mods\autotool.jar"
```

---

## 📅 Supported Minecraft Versions

- **Minecraft 26.4** (Latest)
- **Minecraft 26.3**
- **Minecraft 26.2**
- **Minecraft 26.1**

---

## 🛠 How to Access Settings

You can open the configuration menu at any time by typing either client command in in-game chat:
- **`/autotool`**
- **`/at`**

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
- **Trigger**: A small "**D**" button appears above supported container screens (Chests, Furnaces, etc.).
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
- **Options**:
  - **Numeric Limits**: Set how many targets to show per category (0-5).
  - **Categories**: Monsters, Passive Mobs, Players, Diamond Ore, Emerald Ore, Gold Ore, Iron Ore, Ancient Debris, Chests, and Spawners.
  - **Global Limit**: Automatically displays up to the 5 physically closest targets across all enabled categories.

---

## 🛠 Development & Building

This project uses [Stonecutter](https://stonecutter.kikugie.dev/) to build across multiple Minecraft versions from a single unified codebase on `master`.

### Build All Versions
```bash
./gradlew buildAll
```
Compiled mod JARs will be generated in `versions/<mc-version>/build/libs/`.
### Run Minecraft Client for Testing
```bash
./gradlew run26.4
./gradlew run26.3
./gradlew run26.2
./gradlew run26.1
```

---

## 💡 Inspiration

Inspired by `ControlPack` by uyjulian (<https://github.com/uyjulian/ControlPack>).
