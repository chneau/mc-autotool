package chneau.autotool;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class Autoswap implements UseBlockCallback {

    public void register() {
        UseBlockCallback.EVENT.register(this);
    }

    @Override
    public ActionResult interact(PlayerEntity p, World w, Hand h, BlockHitResult bhr) {
        if (!Util.isCurrentPlayer(p))
            return ActionResult.PASS;
        if (h != Hand.MAIN_HAND)
            return ActionResult.PASS;
        ItemStack itemStack = p.getInventory().main.get(p.getInventory().selectedSlot);
        int maxCount = itemStack.getMaxCount();
        int count = itemStack.getCount();
        if (count == maxCount)
            return ActionResult.PASS;
        p.getInventory().removeStack(1, 2);
        return ActionResult.PASS;
    }

}
