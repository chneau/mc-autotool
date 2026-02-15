# Suggested Improvements for mc-autotool

## New Feature Ideas

- [ ] **14. `AutoTotem`**
  - Automatically moves a Totem of Undying to the off-hand.
  - **Default**: `SMART`
  - **Modes**:
    - `OFF`: Disabled.
    - `ALWAYS`: Keeps a totem in the off-hand if one is available in inventory.
    - `SMART`: Only moves a totem to the off-hand if health falls below 40% (4 hearts).

- [ ] **15. `AutoNoBreak` (Durability Protection)**
  - Prevents tools from breaking by monitoring durability.
  - **Modes**:
    - `OFF`: Disabled.
    - `NOTIFY`: Sends a warning when durability is below 5%.
    - `SWITCH`: Automatically switches to another tool or empty slot when durability is low.
    - `STOP`: Cancels the mining/attacking action to prevent the tool from breaking.

- [ ] **16. `AutoLight`**
  - Automatically places a torch when it gets too dark.
  - **Default**: `OFF`
  - **Modes**:
    - `OFF`: Disabled.
    - `PLAYER`: Places a torch at the player's current position when light level is < 7.
    - `TARGET`: Places a torch on the targeted block if the light level there is low.

- [ ] **18. `AutoTool: Enchantment Strategy`**
  - Enhance `AutoSwap` to choose between Silk Touch and Fortune based on the block.
  - **Logic**: Use Silk Touch for blocks like Glass, Ice, or Grass; use Fortune for ores like Diamond or Coal.

- [ ] **19. `AutoShield`**
  - Automatically raises your shield when danger is detected.
  - **Logic**: Blocks when an arrow is flying towards you or when an entity is within 3 blocks and mid-swing.

- [ ] **21. `AutoTrash`**
  - Automatically drops items that you don't want.
  - **Logic**: Users can define a "trash list" (e.g., Dirt, Cobblestone, Rotten Flesh) and the mod will drop them as soon as they enter the inventory.

- [ ] **22. `AutoRespawn`**
  - Automatically clicks the "Respawn" button when you die.

- [ ] **23. `AutoReconnect`**
  - Automatically attempts to reconnect to a server if you are disconnected.
  - **Settings**: Customizable delay and max attempts.

- [ ] **24. `AutoWalk`**
  - Keeps the player walking forward without holding the key.
  - **Logic**: A simple toggle that simulates the "W" key being pressed.

- [ ] **25. `AutoMine`**
  - Automatically holds down the attack/mine key while looking at a breakable block.
  - **Logic**: Use the current `AutoSwap` logic to pick the tool, then keep mining until the block is gone.

- [ ] **26. `AutoClicker`**
  - Automatically clicks at a set interval (CPS).
  - **Settings**: Adjustable clicks per second. Works for both left and right click.

- [ ] **27. `AutoBridge`**
  - Automatically places a block under the player's feet when walking near an edge.
  - **Logic**: Checks if the player is about to fall and if they are holding a placeable block.

- [ ] **29. `AutoMount`**
  - Automatically mounts a nearby Horse, Boat, or Minecart if empty.
  - **Logic**: Triggers when walking into the entity's hitbox.

- [ ] **30. `AutoLeave`**
  - Automatically disconnects from a server if health drops below a critical threshold.
  - **Settings**: Health percentage threshold (e.g., 10% or 2 hearts).

## Optimisations

- [ ] **7. `AutoAttack` Attribute Caching**
  - **Issue**: Re-calculates `ATTACK_SPEED` attribute and modifiers every tick.
  - **Proposed Fix**: Cache the calculated attack delay and only update it when the item in the main hand changes.

- [ ] **8. `AutoSort` Packet Optimization**
  - **Issue**: Uses a basic swap algorithm that sends 3 packets per swap, potentially flooding the server during a full inventory sort.
  - **Proposed Fix**: Implement a more efficient sorting algorithm (like a Cycle Sort variation) to minimize the total number of item moves.

- [ ] **9. `AutoDeposit` Set Efficiency**
  - **Issue**: Uses a `HashSet<ItemStack>` but performs a manual O(N) loop for comparison, negating the benefits of a Set.
  - **Proposed Fix**: Use a `Set<Item>` for O(1) lookups during the "Smart" check.

- [ ] **10. `AutoStep` Event-Driven Updates**
  - **Issue**: Sets the `STEP_HEIGHT` attribute every single tick.
  - **Proposed Fix**: Only update the attribute when the player spawns or when the configuration is changed.

- [ ] **11. `AutoTarget` Threading Refinement**
  - **Issue**: Uses `synchronized` blocks which can cause minor contention between the background scan and the render thread.
  - **Proposed Fix**: Use `AtomicReference` or a `volatile` result object to swap the scan results atomically without locking.
