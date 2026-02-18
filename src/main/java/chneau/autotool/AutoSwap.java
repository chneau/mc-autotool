package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;

public class AutoSwap extends BaseModule
		implements
			Safe.PlayerAttackBlock,
			Safe.PlayerAttackEntity,
			ClientTickEvents.EndTick {
	private int last = -1;
	private final Select best = Select.best(), first = Select.first();
	private ItemStack lastHeld = ItemStack.EMPTY;
	private BlockPos lastBreaking = null;

	public AutoSwap() {
		super("AutoSwap");
	}

	private Select getS() {
		return config().autoSwap == Config.Strategy.BEST ? best : first;
	}

	@Override
	public InteractionResult interact(Player p, Level w, InteractionHand h, BlockPos pos, Direction d) {
		if (config().autoSwap == Config.Strategy.OFF || h != InteractionHand.MAIN_HAND)
			return InteractionResult.PASS;
		lastBreaking = pos;
		if (last == -1)
			last = p.getInventory().getSelectedSlot();
		int t = getS().selectTool(p.getInventory(), w.getBlockState(pos));
		if (t != -1) {
			if (p.getInventory().getSelectedSlot() != t)
				Util.selectSlot(client(), t);
			return InteractionResult.PASS;
		}
		int any = getS().selectAnyTool(p.getInventory(), w.getBlockState(pos));
		if (any != -1)
			Util.swap(client(), p.inventoryMenu.containerId, any, p.getInventory().getSelectedSlot());
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interact(Player p, Level w, InteractionHand h, Entity e, EntityHitResult ehr) {
		if (config().autoSwap == Config.Strategy.OFF || h != InteractionHand.MAIN_HAND)
			return InteractionResult.PASS;
		if (last == -1)
			last = p.getInventory().getSelectedSlot();
		int s = getS().selectWeapon(p.getInventory());
		if (s != -1) {
			if (p.getInventory().getSelectedSlot() != s) {
				last = s;
				Util.selectSlot(client(), s);
			}
			return InteractionResult.PASS;
		}
		int any = getS().selectAnyWeapon(p.getInventory());
		if (any != -1)
			Util.swap(client(), p.inventoryMenu.containerId, any, p.getInventory().getSelectedSlot());
		return InteractionResult.PASS;
	}

	@Override
	public void onEndTick(Minecraft c) {
		var p = c.player;
		if (p.getInventory() == null)
			return;
		var held = p.getMainHandItem();
		if (held.isEmpty() && !lastHeld.isEmpty() && c.mouseHandler.isLeftPressed()) {
			int r = (lastBreaking != null && c.level != null)
					? getS().selectAnyTool(p.getInventory(), c.level.getBlockState(lastBreaking))
					: getS().selectAnyWeapon(p.getInventory());
			if (r != -1)
				Util.swap(c, p.inventoryMenu.containerId, r, p.getInventory().getSelectedSlot());
		}
		lastHeld = held.copy();
		if (c.hitResult == null)
			lastBreaking = null;
		boolean lb = c.mouseHandler.isLeftPressed();
		if (!lb) {
			if (last != -1)
				Util.selectSlot(client(), last);
			last = -1;
		} else if (last == -1)
			last = p.getInventory().getSelectedSlot();
	}
}
