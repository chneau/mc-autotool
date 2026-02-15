# Todo

## Features
- [ ] **AutoTarget Enhancements**
    - [ ] Customizable HUD (color, background opacity).
    - [ ] Option to show more than 5 targets.
    - [ ] Improve sorting of targets (prioritize based on type, e.g., Hostile > Diamond > Passive).
    - [ ] Add more block types to scan (Chests, Spawners, Ancient Debris, etc.).
- [ ] **AutoSwap Improvements**
    - [ ] Add "Auto-Tool" support for continuous block breaking (reactive swapping during mining).
    - [ ] Support for secondary tool uses (e.g., using a shovel for path-making).
- [ ] **AutoArmor Enhancements**
    - [ ] Better "Smart" mode: Consider specific enchantments (Protection, Feather Falling, Mending).
    - [ ] Auto-equip Elytra when falling and swap back when on ground.
- [ ] **AutoEat Enhancements**
    - [ ] Check full inventory for food, not just hotbar (auto-move to hotbar or offhand).
    - [ ] Support for "Eat while moving" (optional).
    - [ ] Better food prioritization (consider saturation vs. nutrition).
- [ ] **AutoSort Improvements**
    - [ ] Support for sorting external containers (Chests, Barrels, etc.).
    - [ ] Customizable sort orders via config.
- [ ] **AutoDeposit Enhancements**
    - [ ] Smart depositing: Only deposit items that are already present in the target container.
- [ ] **New Modules**
    - [ ] **Auto-Shield:** Automatically block with shield when an entity is attacking or a projectile is incoming.
    - [ ] **Auto-Loot:** Automatically move items from containers to inventory based on configurable filters.
    - [ ] **Auto-Light:** Automatically place torches in low light levels when held.

## Optimizations
- [ ] **Scanner Optimization**
    - [ ] Optimize `Scanner.scanBlocks`. Instead of a full $33 \times 33 \times 33$ cube, use a more efficient approach (e.g., incremental scanning, shell-based scanning).
    - [ ] Cache block scan results and only update when player moves a certain distance or blocks change.
- [ ] **General Performance**
    - [ ] Reduce redundant calculations in `AutoTarget` and other HUD-rendering modules.
    - [ ] Use `BlockPos.betweenClosed` or similar for better iteration performance if applicable.

## Refactoring
- [ ] **Modular Initialization**
    - [ ] Use a more structured way to register modules in `Main.java`.
- [ ] **Inventory API**
    - [ ] Improve `Util.java` with more robust and reusable inventory manipulation methods.
    - [ ] Standardize item comparison and "better item" logic across modules.
- [ ] **Logging**
    - [ ] Add meaningful debug logging (using the mod's logger) for module actions.

## UI/UX
- [ ] **Config Screen Improvements**
    - [ ] Better organization of settings (categories).
    - [ ] Tooltips for different modes to explain what they do.
