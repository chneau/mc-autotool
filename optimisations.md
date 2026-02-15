# Performance Optimisations for mc-autotool

This document identifies CPU-intensive code paths and proposes optimisations to ensure the mod remains lightweight, even in complex environments.

## Items

- [ ] **1. `AutoTarget` Entity Scanning**
  - **Issue**: Loops through `entitiesForRendering()` every HUD render frame.
  - **Proposed Fix**: Cache the closest entities once every 10-20 ticks instead of every frame.

- [ ] **2. `AutoTarget` Block Scanning**
  - **Issue**: `scanBlocks` iterates through ~36,000 blocks every 2 seconds on the main thread.
  - **Proposed Fix**: Move to a background thread or use a fragmented scan (few layers per tick).

- [ ] **3. `AutoEat` Input Polling**
  - **Issue**: Iterates through 300+ keys every tick using `glfwGetKey`.
  - **Proposed Fix**: Only perform the check if movement/look state has changed.

- [ ] **4. `AutoArmor` Inventory Loops**
  - **Issue**: Scans entire inventory multiple times every 200ms.
  - **Proposed Fix**: Only trigger when the inventory actually changes or cache the best items.

- [ ] **5. `AutoFarm` Redundant Loops**
  - **Issue**: Performs full area scan every tick even if looking at the same block.
  - **Proposed Fix**: Only trigger area scan if the targeted `BlockPos` has changed.

- [ ] **6. Centralized Tick Throttling**
  - **Issue**: Many modules run logic every 50ms (every tick) unnecessarily.
  - **Proposed Fix**: Implement a `Throttler` utility to spread heavy tasks across different ticks.
