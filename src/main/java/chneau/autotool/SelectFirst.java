package chneau.autotool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;

public class SelectFirst implements Select {
    @Override
    public int selectTool(PlayerInventory inventory, BlockState bState) {
        var targetItem = bState.getBlock().asItem();
        return HOTBAR_SUPPLIER.get().filter(i -> {
            var item = inventory.main.get(i).getItem();
            if (item instanceof MiningToolItem == false)
                return false;
            if (item.getMiningSpeedMultiplier(new ItemStack(targetItem), bState) > 1)
                return true;
            return false;
        }).findFirst().orElse(-1);
    }

    @Override
    public int selectWeapon(PlayerInventory inventory) {
        return HOTBAR_SUPPLIER.get().filter(i -> inventory.main.get(i).getItem() instanceof SwordItem).findFirst()
                .orElse(-1);
    }
}
