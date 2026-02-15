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
