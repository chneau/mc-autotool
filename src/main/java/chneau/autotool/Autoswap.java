package chneau.autotool;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class Autoswap implements UseBlockCallback {

    public void register() {
        UseBlockCallback.EVENT.register(this);
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult bhr) {
        if (!Util.isCurrentPlayer(player))
            return ActionResult.PASS;
        if (hand != Hand.MAIN_HAND)
            return ActionResult.PASS;
        var itemStack = player.getInventory().main.get(player.getInventory().selectedSlot);
        var maxCount = itemStack.getMaxCount();
        var count = itemStack.getCount();
        if (count == maxCount)
            return ActionResult.PASS;
        player.getInventory().removeStack(1, 2);
        return ActionResult.PASS;
    }

}
