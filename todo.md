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
  - Prioritizes food based on saturation or nutritional value.

- [ ] **12. `AutoFish`**
  - Automatically reels in and recasts the fishing rod when a fish is caught.

- [ ] **13. `AutoArmor`**
  - Automatically equips the best armor pieces from your inventory.

- [ ] **14. `AutoTotem`**
  - Automatically moves a Totem of Undying to the off-hand if the current one is used or missing.

- [ ] **15. `AutoToolRepair` (Durability Protection)**
  - Automatically stops using a tool (or switches to another) when its durability is extremely low to prevent it from breaking.

- [ ] **16. `AutoLight`**
  - Automatically places a torch when the light level at the player's position is too low.
