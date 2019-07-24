package chneau.autotool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;

/**
 * ISelect
 */
public interface ISelect {
    public Integer selectTool(PlayerInventory inventory, BlockState bState);

    public Integer selectWeapon(PlayerInventory inventory);
}
