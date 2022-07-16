package chneau.autotool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;

public class SelectFirst implements Select {
    @Override
    public int selectTool(PlayerInventory inventory, BlockState blockState) {
        var targetItem = blockState.getBlock().asItem();

        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var item = inventory.main.get(i).getItem();
            if (!(item instanceof MiningToolItem))
                continue;
            if (item.getMiningSpeedMultiplier(new ItemStack(targetItem), blockState) > 1)
                return i;
        }

        return -1;
    }

    @Override
    public int selectWeapon(PlayerInventory inventory) {
        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var item = inventory.main.get(i).getItem();
            if (item instanceof SwordItem)
                return i;
        }

        return -1;
    }
}
