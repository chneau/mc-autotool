package chneau.autotool;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

public class Autoswap implements UseBlockCallback {

    public void register() {
        UseBlockCallback.EVENT.register(this);
    }

    @Override
    public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult bhr) {
        if (!Util.isCurrentPlayer(player))
            return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;
        var itemStack = player.getInventory().getItem(player.getInventory().getSelectedSlot());
        var maxCount = itemStack.getMaxStackSize();
        var count = itemStack.getCount();
        if (count == maxCount)
            return InteractionResult.PASS;
        player.getInventory().removeItem(1, 2);
        return InteractionResult.PASS;
    }

}
