package chneau.autotool;

import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import java.util.function.Supplier;

/**
 * ISelect
 */
public interface ISelect {
    static final int HOTBAR_SIZE = PlayerInventory.getHotbarSize();
    static final Supplier<IntStream> HOTBAR_SUPPLIER = () -> IntStream.range(0, HOTBAR_SIZE);

    public int selectTool(PlayerInventory inventory, BlockState bState);

    public int selectWeapon(PlayerInventory inventory);
}
