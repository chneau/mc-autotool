# Autotool

A powerful utility mod for Minecraft that automates tedious tasks like tool swapping, farming, eating, and inventory management.

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
- **Trigger**: Looking at a living entity or an enemy being nearby.
- **Options**:
  - `OFF`: Disable auto-attack.
  - `SWORD`: Only auto-attacks when you are holding a sword.
  - `OMNI`: (Default) Auto-attacks enemies with the best sword (switches automatically). For non-enemies, requires holding a sword and looking at them.

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

## 📜 License
This project is licensed under the MIT License.
