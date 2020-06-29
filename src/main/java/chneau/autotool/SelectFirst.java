package chneau.autotool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;

public class SelectFirst implements Select {
    @Override
    public int selectTool(PlayerInventory inv, BlockState bState) {
        Item targetItem = bState.getBlock().asItem();
        return HOTBAR_SUPPLIER.get().filter(i -> {
            Item item = inv.main.get(i).getItem();
            if (item instanceof MiningToolItem == false)
                return false;
            if (item.getMiningSpeedMultiplier(new ItemStack(targetItem), bState) > 1)
                return true;
            return false;
        }).findFirst().orElse(-1);
    }

    @Override
    public int selectWeapon(PlayerInventory inv) {
        return HOTBAR_SUPPLIER.get().filter(i -> inv.main.get(i).getItem() instanceof SwordItem).findFirst().orElse(-1);
    }
}
