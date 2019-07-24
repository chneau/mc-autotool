package chneau.autotool;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

/**
 * BestSelect
 */
public class BestSelect implements ISelect {

    @Override
    public Integer selectTool(PlayerInventory inventory, BlockState bState) {
        throw new Error("Unimplemented");
    }

    @Override
    public Integer selectWeapon(PlayerInventory inventory) {
        throw new Error("Unimplemented");
    }

}
