package chneau.autotool;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;

public class SelectFirst implements Select {
    @Override
    public int selectTool(Inventory inventory, BlockState blockState) {
        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var stack = inventory.getItem(i);
            if (!(stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.AXES) || stack.is(ItemTags.SHOVELS) || stack.is(ItemTags.HOES)))
                continue;
            if (stack.getDestroySpeed(blockState) > 1)
                return i;
        }

        return -1;
    }

    @Override
    public int selectWeapon(Inventory inventory) {
        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var stack = inventory.getItem(i);
            if (stack.is(ItemTags.SWORDS))
                return i;
        }

        return -1;
    }
}
