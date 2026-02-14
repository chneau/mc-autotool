package chneau.autotool;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;

public class Autoswap implements UseBlockCallback {

    public void register() {
        UseBlockCallback.EVENT.register(this);
    }

    @Override
    public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult bhr) {
        if (!ConfigManager.getConfig().autoRefillEnabled)
            return InteractionResult.PASS;
        if (!Util.isCurrentPlayer(player))
            return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;
        
        var inventory = player.getInventory();
        var selectedSlot = inventory.getSelectedSlot();
        var itemStack = inventory.getItem(selectedSlot);
        
        if (itemStack.isEmpty()) return InteractionResult.PASS;

        if (itemStack.getCount() > 1)
            return InteractionResult.PASS;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (i == selectedSlot) continue;
            
            var candidate = inventory.getItem(i);
            if (!candidate.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, candidate)) {
                inventory.setItem(selectedSlot, candidate.copy());
                inventory.setItem(i, ItemStack.EMPTY);
                break;
            }
        }
        
        return InteractionResult.PASS;
    }

}
