package chneau.autotool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;

/**
 * BestSelect
 */
public class BestSelect implements ISelect {

    @Override
    public int selectTool(PlayerInventory inventory, BlockState bState) {
        throw new Error("Unimplemented");
    }

    @Override
    public int selectWeapon(PlayerInventory inventory) {
        throw new Error("Unimplemented");
    }

}
