package chneau.autotool;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
public class AutoRefill extends BaseModule implements Safe.PlayerUseBlock {
	private ItemStack last = ItemStack.EMPTY;
	@Override
	public InteractionResult interact(Player p, Level w, InteractionHand h, BlockHitResult b) {
		var mode = config().autoRefill;
		if (mode == Config.RefillMode.OFF || h != InteractionHand.MAIN_HAND
				|| w.getBlockState(b.getBlockPos()).getBlock() instanceof AbstractFurnaceBlock)
			return InteractionResult.PASS;
		var inv = p.getInventory();
		int sIdx = inv.getSelectedSlot();
		var stack = inv.getItem(sIdx);
		var target = stack.isEmpty() ? last : stack;
		if (target.isEmpty() || (mode == Config.RefillMode.SMART && !stack.isEmpty() && stack.getCount() > 1)) {
			if (!stack.isEmpty())
				last = stack.copy();
			return InteractionResult.PASS;
		}
		for (int i = 0; i < inv.getContainerSize(); i++)
			if (i != sIdx && !inv.getItem(i).isEmpty() && Util.areItemsEqual(target, inv.getItem(i))) {
				inv.setItem(sIdx, inv.getItem(i).copy());
				inv.setItem(i, ItemStack.EMPTY);
				break;
			}
		last = inv.getItem(sIdx).copy();
		return InteractionResult.PASS;
	}
}
