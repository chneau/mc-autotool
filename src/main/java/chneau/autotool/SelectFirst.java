package chneau.autotool;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

/**
 * SelectFirst
 */
public class SelectFirst implements ISelect {

    @Override
    public Integer selectTool(PlayerInventory inventory, BlockState bState) {
        Block block = bState.getBlock();
        Item targetItem = block.asItem();
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inventory.main.get(i);
            Item item = itemStack.getItem();
            if (item instanceof ToolItem) {
                float miningSpeed = item.getMiningSpeed(new ItemStack(targetItem), bState);
                if (miningSpeed > 1) {
                    return i;
                }
            }
        }
        return null;
    }

    @Override
    public Integer selectWeapon(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inventory.main.get(i);
            Item item = itemStack.getItem();
            if (item instanceof SwordItem) {
                return i;
            }
        }
        return null;
    }

}
