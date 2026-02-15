package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class AutoSwap implements AttackBlockCallback, AttackEntityCallback, EndTick {
	private int last = -1;
	private final Select best = new SelectBest();
	private final Select first = new SelectFirst();

	public AutoSwap() {
	}

	private Select getSelect() {
		return ConfigManager.getConfig().autoSwap == Config.Strategy.BEST ? best : first;
	}

	public void register() {
		AttackBlockCallback.EVENT.register(this);
		AttackEntityCallback.EVENT.register(this);
		ClientTickEvents.END_CLIENT_TICK.register(this);
	}

	@Override
	public InteractionResult interact(Player player, Level world, InteractionHand hand, BlockPos blockPos,
			Direction direction) {
		if (ConfigManager.getConfig().autoSwap == Config.Strategy.OFF)
			return InteractionResult.PASS;
		if (!Util.isCurrentPlayer(player))
			return InteractionResult.PASS;
		if (hand != InteractionHand.MAIN_HAND)
			return InteractionResult.PASS;
		if (last == -1)
			last = player.getInventory().getSelectedSlot();
		var bState = world.getBlockState(blockPos);
		var tool = getSelect().selectTool(player.getInventory(), bState);
		if (tool == -1 || player.getInventory().getSelectedSlot() == tool)
			return InteractionResult.PASS;
		updateServer(tool);
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interact(Player player, Level world, InteractionHand hand, Entity entity,
			EntityHitResult ehr) {
		if (ConfigManager.getConfig().autoSwap == Config.Strategy.OFF)
			return InteractionResult.PASS;
		if (!Util.isCurrentPlayer(player))
			return InteractionResult.PASS;
		if (hand != InteractionHand.MAIN_HAND)
			return InteractionResult.PASS;
		if (last == -1)
			last = player.getInventory().getSelectedSlot();
		var sword = getSelect().selectWeapon(player.getInventory());
		if (sword == -1 || player.getInventory().getSelectedSlot() == sword)
			return InteractionResult.PASS;
		last = sword;
		updateServer(sword);
		return InteractionResult.PASS;
	}

	@Override
	public void onEndTick(Minecraft client) {
		var player = client.player;
		if (player == null || client.hitResult == null || player.getInventory() == null)
			return;
		if (!Util.isCurrentPlayer(player))
			return;
		updateLast(player.getInventory(), client.mouseHandler.isLeftPressed());
	}

	private void updateLast(Inventory inventory, boolean lbClicked) {
		if (!lbClicked) {
			if (last != -1)
				this.updateServer(last);
			last = -1;
		} else if (last == -1) {
			last = inventory.getSelectedSlot();
		}
	}

	private void updateServer(int pos) {
		var instance = Minecraft.getInstance();
		var player = instance.player;
		if (player == null)
			return;
		if (player.getInventory().getSelectedSlot() == pos)
			return;
		player.getInventory().setSelectedSlot(pos);
		if (player.connection == null)
			return;
		player.connection.send(new ServerboundSetCarriedItemPacket(pos));
	}
}
