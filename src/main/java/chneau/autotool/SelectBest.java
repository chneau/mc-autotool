package chneau.autotool;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;

public class SelectBest implements Select {
	private int cachedWeaponIndex = -1;
	private int lastInventoryChangeCount = -1;

	@Override
	public int selectTool(Inventory inventory, BlockState blockState) {
		var bestSpeed = 1.;
		var bestIndex = -1;
		for (var i = 0; i < HOTBAR_SIZE; i++) {
			var stack = inventory.getItem(i);
			var speed = stack.getDestroySpeed(blockState);
			if (bestSpeed < speed) {
				bestSpeed = speed;
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	@Override
	public int selectWeapon(Inventory inventory) {
		if (lastInventoryChangeCount == inventory.getTimesChanged() && cachedWeaponIndex != -1) {
			return cachedWeaponIndex;
		}

		var bestDPS = 4.;
		var bestIndex = -1;
		for (var i = 0; i < HOTBAR_SIZE; i++) {
			var stack = inventory.getItem(i);

			var dps = Util.getWeaponDamage(stack) * Util.getWeaponSpeed(stack);
			if (bestDPS < dps) {
				bestDPS = dps;
				bestIndex = i;
			}
		}

		cachedWeaponIndex = bestIndex;
		lastInventoryChangeCount = inventory.getTimesChanged();
		return bestIndex;
	}
}
