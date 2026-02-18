package chneau.autotool;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;

public class AutoRefill extends BaseModule implements Safe.PlayerUseBlock {
	private ItemStack lastHeld = ItemStack.EMPTY;

	public AutoRefill() {
		super("AutoRefill");
	}

	@Override
	public InteractionResult interact(Player p, Level w, InteractionHand h, BlockHitResult bhr) {
		var mode = config().autoRefill;
		if (mode == Config.RefillMode.OFF || h != InteractionHand.MAIN_HAND
				|| w.getBlockState(bhr.getBlockPos()).getBlock() instanceof AbstractFurnaceBlock)
			return InteractionResult.PASS;
		var inv = p.getInventory();
		var slot = inv.getSelectedSlot();
		var stack = inv.getItem(slot);
		var target = stack.isEmpty() ? lastHeld : stack;
		if (target.isEmpty() || (mode == Config.RefillMode.SMART && !stack.isEmpty() && stack.getCount() > 1)) {
			if (!stack.isEmpty())
				lastHeld = stack.copy();
			return InteractionResult.PASS;
		}
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (i == slot)
				continue;
			var candidate = inv.getItem(i);
			if (!candidate.isEmpty() && Util.areItemsEqual(target, candidate)) {
				inv.setItem(slot, candidate.copy());
				inv.setItem(i, ItemStack.EMPTY);
				break;
			}
		}
		lastHeld = inv.getItem(slot).copy();
		return InteractionResult.PASS;
	}
}
