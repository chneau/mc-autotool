package chneau.autotool;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.tags.ItemTags;
public class SelectFirst implements Select {
	@Override
	public int selectTool(Inventory inventory, BlockState blockState) {
		return selectAnyToolInContainer(inventory, blockState, HOTBAR_SIZE);
	}
	@Override
	public int selectAnyTool(Inventory inventory, BlockState blockState) {
		return selectAnyToolInContainer(inventory, blockState, inventory.getContainerSize());
	}
	private int selectAnyToolInContainer(Inventory inventory, BlockState blockState, int limit) {
		for (var i = 0; i < limit; i++) {
			var stack = inventory.getItem(i);
			if (!(stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.AXES) || stack.is(ItemTags.SHOVELS)
					|| stack.is(ItemTags.HOES)))
				continue;
			if (stack.getDestroySpeed(blockState) > 1)
				return i;
		}
		return -1;
	}
	@Override
	public int selectWeapon(Inventory inventory) {
		return selectAnyWeaponInContainer(inventory, HOTBAR_SIZE);
	}
	@Override
	public int selectAnyWeapon(Inventory inventory) {
		return selectAnyWeaponInContainer(inventory, inventory.getContainerSize());
	}
	private int selectAnyWeaponInContainer(Inventory inventory, int limit) {
		for (var i = 0; i < limit; i++) {
			var stack = inventory.getItem(i);
			if (stack.is(ItemTags.SWORDS))
				return i;
		}
		return -1;
	}
}
