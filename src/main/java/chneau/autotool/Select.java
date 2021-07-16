package chneau.autotool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;

public interface Select {
    static final int HOTBAR_SIZE = PlayerInventory.getHotbarSize();

    public int selectTool(PlayerInventory inventory, BlockState bState);

    public int selectWeapon(PlayerInventory inventory);
}
