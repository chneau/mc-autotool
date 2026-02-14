# Suggested Improvements for mc-autotool

- [x] **1. Refactor `Autofarm.java` logic**
  - Use a static array of `BlockPos` offsets and iterate through them.
  - Reduces code duplication and makes it easier to change the farm radius or pattern.

- [x] **2. Eliminate Magic Numbers**
  - Move the hardcoded `625` in `Autoattack.java` to a `private static final long ATTACK_DELAY_MS = 625;` constant.
  - Calculate delay based on the current item's attack speed attribute to support different weapons.

- [x] **3. Fix `Autoswap.java` logic**
  - Replace `player.getInventory().removeItem(1, 2);` with actual "Autorefill" logic.
  - Search the inventory for a matching `ItemStack` and move it to the hotbar when the current stack is low.

- [x] **4. Optimize `SelectBest.java`**
  - Consider caching the "best" weapon index and only updating it when the inventory changes.
  - Minor performance gain and cleaner logic.

- [x] **5. Improve `Util.getTargetedBlock`**
  - Investigate using `BlockHitResult` from `client.hitResult` directly or using `RaycastContext`.
  - More "idiomatic" Minecraft modding and potentially more accurate targeting.

- [ ] **6. Configuration System**
  - Implement a configuration file (e.g., Cloth Config or JSON) to allow users to toggle features and adjust values.

- [ ] **7. `Autotool.java` Packet Efficiency**
  - Ensure `ServerboundSetCarriedItemPacket` is only sent if the slot has actually changed.

- [ ] **8. General Code Style**
  - Use `@Override` consistently.
  - Use a logger instead of `System.out.println`.
  - Add comments explaining complex logic, such as the coordinate math in `Util.java`.
