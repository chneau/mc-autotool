package chneau.autotool;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;

public interface Select {
	static final int HOTBAR_SIZE = Inventory.getSelectionSize();

	public int selectTool(Inventory inventory, BlockState bState);

	public int selectWeapon(Inventory inventory);

	public int selectAnyTool(Inventory inventory, BlockState bState);

	public int selectAnyWeapon(Inventory inventory);
}
