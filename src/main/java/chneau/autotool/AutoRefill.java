package chneau.autotool;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;

public class AutoRefill implements UseBlockCallback {

	public void register() {
		UseBlockCallback.EVENT.register(this);
	}

	@Override
	public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult bhr) {
		var mode = ConfigManager.getConfig().autoRefill;
		if (mode == Config.RefillMode.OFF)
			return InteractionResult.PASS;
		if (!Util.isCurrentPlayer(player))
			return InteractionResult.PASS;
		if (hand != InteractionHand.MAIN_HAND)
			return InteractionResult.PASS;

		var state = world.getBlockState(bhr.getBlockPos());
		if (state.getBlock() instanceof AbstractFurnaceBlock) {
			return InteractionResult.PASS;
		}

		var inventory = player.getInventory();
		var selectedSlot = inventory.getSelectedSlot();
		var itemStack = inventory.getItem(selectedSlot);

		if (itemStack.isEmpty())
			return InteractionResult.PASS;

		if (mode == Config.RefillMode.SMART && itemStack.getCount() > 1)
			return InteractionResult.PASS;

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (i == selectedSlot)
				continue;

			var candidate = inventory.getItem(i);
			if (!candidate.isEmpty() && Util.areItemsEqual(itemStack, candidate)) {
				inventory.setItem(selectedSlot, candidate.copy());
				inventory.setItem(i, ItemStack.EMPTY);
				break;
			}
		}

		return InteractionResult.PASS;
	}

}
