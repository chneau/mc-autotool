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
        if (h != Hand.MAIN_HAND)
            return ActionResult.PASS;
        ItemStack itemStack = p.inventory.main.get(p.inventory.selectedSlot);
        int maxCount = itemStack.getMaxCount();
        int count = itemStack.getCount();
        System.out.println(maxCount + " " + count);
        // p.inventory.main.
        return ActionResult.PASS;
    }

}
