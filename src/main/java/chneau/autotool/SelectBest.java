package chneau.autotool;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;

public class SelectBest implements Select {
	private int cachedWeaponIndex = -1;
	private int lastInventoryChangeCount = -1;

	@Override
	public int selectTool(Inventory inventory, BlockState blockState) {
		return selectInContainer(inventory, blockState, HOTBAR_SIZE);
	}

	@Override
	public int selectAnyTool(Inventory inventory, BlockState blockState) {
		return selectInContainer(inventory, blockState, inventory.getContainerSize());
	}

	private int selectInContainer(Inventory inventory, BlockState blockState, int limit) {
		var bestSpeed = 1.;
		var bestIndex = -1;
		for (var i = 0; i < limit; i++) {
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
		cachedWeaponIndex = selectAnyWeaponInContainer(inventory, HOTBAR_SIZE);
		lastInventoryChangeCount = inventory.getTimesChanged();
		return cachedWeaponIndex;
	}

	@Override
	public int selectAnyWeapon(Inventory inventory) {
		return selectAnyWeaponInContainer(inventory, inventory.getContainerSize());
	}

	private int selectAnyWeaponInContainer(Inventory inventory, int limit) {
		var bestDPS = 4.;
		var bestIndex = -1;
		for (var i = 0; i < limit; i++) {
			var stack = inventory.getItem(i);

			var dps = Util.getWeaponDamage(stack) * Util.getWeaponSpeed(stack);
			if (bestDPS < dps) {
				bestDPS = dps;
				bestIndex = i;
			}
		}
		return bestIndex;
	}
}
