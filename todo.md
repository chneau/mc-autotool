# Todo

## Features
- [x] **AutoTarget Enhancements**
    - [x] Customizable HUD (color).
    - [x] Option to show more than 5 targets.
    - [x] Improve sorting of targets (prioritize based on type, e.g., Hostile > Diamond > Passive).
    - [x] Add more block types to scan (Chests, Spawners, Ancient Debris, etc.).
    - [x] **3D Direction Arrow:** Replaced text arrow with a fully 3D, shaded, rotating cylinder/cone model.
- [ ] **AutoFarm Enhancements**
    - [ ] **Bonemeal Support:** Automatically use bonemeal on crops.
    - [ ] **Replant Logic:** Ensure seeds are available before breaking crops.
- [ ] **AutoRefill Enhancements**
    - [ ] Support offhand refill.
    - [ ] Configurable threshold for refill (currently waits until empty or low).
- [ ] **AutoSprint Enhancements**
    - [ ] "Smart Sprint": Stop sprinting if hunger is low or close to a wall/obstacle.
- [ ] **AutoSwap Improvements**
    - [ ] **Tool Preference:** Prioritize Silk Touch vs Fortune based on block type (e.g., Ore vs Stone).
    - [ ] Add "Auto-Tool" support for continuous block breaking (reactive swapping during mining).
    - [ ] Support for secondary tool uses (e.g., using a shovel for path-making).
- [ ] **AutoArmor Enhancements**
    - [ ] Better "Smart" mode: Consider specific enchantments (Protection, Feather Falling, Mending).
    - [ ] **Safety Check:** Avoid equipping items with "Curse of Binding".
    - [ ] Auto-equip Elytra when falling and swap back when on ground.
- [ ] **AutoEat Enhancements**
    - [ ] Check full inventory for food, not just hotbar (auto-move to hotbar or offhand).
    - [ ] Support for "Eat while moving" (optional).
    - [ ] Better food prioritization (consider saturation vs. nutrition).
- [ ] **AutoSort Improvements**
    - [x] Support for sorting external containers (Chests, Barrels, etc.).
    - [ ] Customizable sort orders via config (Name, Quantity, ID, etc.).
    - [ ] Optimize sorting algorithm to minimize packet usage further.
- [ ] **AutoDeposit Enhancements**
    - [ ] Smart depositing: Only deposit items that are already present in the target container.
- [ ] **AutoFish Enhancements**
    - [ ] Support for open water detection.
    - [ ] Auto-switch rods based on durability or enchantments.
- [ ] **New Modules**
    - [ ] **Auto-Shield:** Automatically block with shield when an entity is attacking or a projectile is incoming.
    - [ ] **Auto-Loot:** Automatically move items from containers to inventory based on configurable filters.
    - [ ] **Auto-Light:** Automatically place torches in low light levels when held.
    - [ ] **Auto-Logout:** Automatically disconnect when health is critically low.
    - [ ] **Auto-Mine:** Continue mining if tool breaks by swapping to a fresh one.

## Optimizations
- [ ] **Scanner Optimization**
    - [ ] Optimize `Scanner.scanBlocks`. Instead of a full $33 \times 33 \times 33$ cube, use a more efficient approach (e.g., incremental scanning, shell-based scanning).
    - [ ] Use `BlockPos.betweenClosedStream` or similar to reduce object allocation.
    - [ ] Cache block scan results and only update when player moves a certain distance or blocks change.
- [ ] **General Performance**
    - [ ] Reduce redundant calculations in `AutoTarget` and other HUD-rendering modules.
    - [ ] Cache expensive reflection lookups (e.g. `AutoFish` reflection is already static, but verify others).

## Refactoring
- [ ] **Modular Initialization**
    - [ ] Use a more structured way to register modules in `Main.java` (e.g. a list or service loader).
- [x] **Inventory API**
    - [x] Improve `Util.java` with more robust and reusable inventory manipulation methods.
    - [x] Standardize item comparison and "better item" logic across modules.
- [x] **Code Cleanup**
    - [x] Remove obsolete methods like `AutoTarget.drawInfo`.
    - [x] Refactor `Config` to handle defaults more cleanly.
- [ ] **Logging**
    - [ ] Add meaningful debug logging (using the mod's logger) for module actions.

## CI/CD
- [ ] **GitHub Actions**
    - [ ] Add workflow for automatic build and check (Spotless) on push.

## UI/UX
- [x] **Config Screen Improvements**
    - [ ] Better organization of settings (categories) - *Note: OptionsList does not support headers easily in this version.*
    - [x] Tooltips for different modes to explain what they do.
