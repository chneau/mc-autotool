# Performance Optimisations for mc-autotool

This document identifies CPU-intensive code paths and proposes optimisations to ensure the mod remains lightweight, even in complex environments.

## 1. `AutoTarget` Entity Scanning
- **Issue**: Currently loops through `client.level.entitiesForRendering()` every single HUD render frame (potentially 60-144+ times per second).
- **Impact**: High CPU usage in areas with many entities.
- **Proposed Fix**:
  - Cache the closest entities once every 10-20 ticks (0.5 - 1 second) instead of every frame.
  - The HUD rendering should use the cached list and only update the relative direction (arrow) and distance based on current player position.

## 2. `AutoTarget` Block Scanning
- **Issue**: `scanBlocks` iterates through $33 	imes 33 	imes 33 = 35,937$ blocks every 2 seconds on the main client thread.
- **Impact**: Can cause a noticeable "micro-stutter" or frame drop every 2 seconds.
- **Proposed Fix**:
  - Move block scanning to a background thread.
  - Or, use a "fragmented scan" approach: scan a few layers per tick instead of the whole cube at once.

## 3. `AutoEat` Input Polling
- **Issue**: Iterates through 316 GLFW keys and 8 mouse buttons every tick using `glfwGetKey`.
- **Impact**: Unnecessary system calls every 50ms.
- **Proposed Fix**:
  - Use Fabric's `ClientInputEvents` or similar event-based system if available.
  - Or, only perform the check if the player's movement or look direction has changed (using existing `player` state).

## 4. `AutoArmor` Inventory Loops
- **Issue**: Scans the entire inventory (36 slots) multiple times every 200ms.
- **Impact**: Minor, but adds up with other features.
- **Proposed Fix**:
  - Only trigger armor checks when the inventory actually changes (using `InventoryEvents`).
  - Cache the "best" armor items and only re-calculate when a new equippable item is picked up.

## 5. `AutoFarm` Constant Raycast Logic
- **Issue**: Performs nested loops ($3 	imes 3$ and $3 	imes 2 	imes 3$) every single tick as long as the player is looking at a block.
- **Impact**: Redundant calculations when the player is stationary.
- **Proposed Fix**:
  - Only trigger the area scan if the targeted `BlockPos` has changed from the previous tick.

## 6. General: Tick Throttling
- **Issue**: Many modules implement `EndTick` and run logic every 50ms, even when not strictly necessary.
- **Proposed Fix**:
  - Implement a centralized `Throttler` utility to easily run logic every X ticks.
  - Example: `if (Throttler.shouldRun(this, 10)) { ... }`
