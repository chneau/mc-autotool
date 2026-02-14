# Suggested Improvements for mc-autotool

- [x] **1. Refactor `AutoFarm.java` logic**
  - Use a static array of `BlockPos` offsets and iterate through them.
  - Reduces code duplication and makes it easier to change the farm radius or pattern.

- [x] **2. Eliminate Magic Numbers**
  - Move the hardcoded `625` in `AutoAttack.java` to a `private static final long ATTACK_DELAY_MS = 625;` constant.
  - Calculate delay based on the current item's attack speed attribute to support different weapons.

- [x] **3. Fix `AutoRefill.java` logic**
  - Replace `player.getInventory().removeItem(1, 2);` with actual "Autorefill" logic.
  - Search the inventory for a matching `ItemStack` and move it to the hotbar when the current stack is low.

- [x] **4. Optimize `SelectBest.java`**
  - Consider caching the "best" weapon index and only updating it when the inventory changes.
  - Minor performance gain and cleaner logic.

- [x] **5. Improve `Util.getTargetedBlock`**
  - Investigate using `BlockHitResult` from `client.hitResult` directly or using `RaycastContext`.
  - More "idiomatic" Minecraft modding and potentially more accurate targeting.

- [x] **6. Configuration System**
  - Implement a configuration file (e.g., Cloth Config or JSON) to allow users to toggle features and adjust values.

- [x] **7. `AutoSwap.java` Packet Efficiency**
  - Ensure `ServerboundSetCarriedItemPacket` is only sent if the slot has actually changed.

- [x] **8. General Code Style**
  - Use `@Override` consistently.
  - Use a logger instead of `System.out.println`.
  - Add comments explaining complex logic, such as the coordinate math in `Util.java`.

- [x] **9. AutoSwap Strategy Toggle**
  - Added a configuration option to toggle between "Best" and "First" strategies.

- [x] **10. Settings Shortcut Key**
  - Implement a shortcut key (e.g., `Ctrl + Shift + O`) to open the mod's configuration screen directly.

## New Feature Ideas

- [ ] **11. `AutoEat`**
  - Automatically eats food when hunger falls below a certain threshold.
  - **Logic**: Only triggers after the player has been inactive (not attacking/mining/interacting) for at least **1 second**.
  - **Default**: `SMART`
  - **Modes**:
    - `OFF`: Disabled.
    - `HUNGER`: Eat whenever hunger points are missing.
    - `HEALTH`: Only eat when health is not full (to maintain regeneration).
    - `SMART`: Optimizes food usage based on saturation and hunger levels; **checks food value to avoid over-eating/wasting high-value food**.

- [ ] **12. `AutoFish`**
  - Automatically reels in and recasts the fishing rod when a fish is caught.
  - **Default**: `ON`
  - **Modes**: `OFF`, `ON`.

- [ ] **13. `AutoArmor`**
  - Automatically equips the best armor pieces from your inventory.
  - **Default**: `SMART`
  - **Modes**:
    - `OFF`: Disabled.
    - `BETTER`: Equips armor with higher raw armor value.
    - `SMART`: Considers enchantments (e.g., Protection, Mending) alongside armor value.

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
