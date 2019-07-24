package chneau.autotool;

import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import java.util.function.Supplier;

/**
 * SelectFirst
 */
public class SelectFirst implements ISelect {
    static final Supplier<IntStream> HOTBAR_SUPPLIER = () -> IntStream.range(0, PlayerInventory.getHotbarSize());

    @Override
    public int selectTool(PlayerInventory inv, BlockState bState) {
        Item targetItem = bState.getBlock().asItem();
        return HOTBAR_SUPPLIER.get().filter(i -> {
            Item item = inv.main.get(i).getItem();
            if (item instanceof ToolItem == false)
                return false;
            if (item.getMiningSpeed(new ItemStack(targetItem), bState) > 1)
                return true;
            return false;
        }).findFirst().orElse(-1);
    }

    @Override
    public int selectWeapon(PlayerInventory inv) {
        return HOTBAR_SUPPLIER.get().filter(i -> inv.main.get(i).getItem() instanceof SwordItem).findFirst().orElse(-1);
    }

}
