package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.*;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
public class AutoFarm extends BaseModule implements ClientTickEvents.EndTick {
	private BlockPos lastTarget = null;
	@Override
	public void onEndTick(Minecraft client) {
		if (config().autoFarm == Config.FarmMode.OFF || client.hitResult == null
				|| client.player.getInventory() == null)
			return;
		var target = Util.getTargetedBlock(client);
		if (target == null || target.equals(lastTarget)) {
			lastTarget = target;
			return;
		}
		lastTarget = target;
		var stack = client.player.getMainHandItem();
		if (client.hitResult.getType() == HitResult.Type.BLOCK && (stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS)
				|| stack.is(Items.NETHER_WART) || stack.has(DataComponents.TOOL))) {
			var nh = client.getConnection();
			if (nh == null)
				return;
			var bhr = (BlockHitResult) client.hitResult;
			for (int dx = -1; dx <= 1; dx++)
				for (int dz = -1; dz <= 1; dz++)
					harvest(client, nh, target.offset(dx, 0, dz), bhr);
			if (config().autoFarm == Config.FarmMode.BOTH
					&& (stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) || stack.is(Items.NETHER_WART))) {
				for (int dy = -1; dy <= 0; dy++)
					for (int dx = -1; dx <= 1; dx++)
						for (int dz = -1; dz <= 1; dz++)
							plant(client, nh, bhr.getBlockPos().offset(dx, dy, dz), bhr);
			}
		}
	}
	private void plant(Minecraft c, net.minecraft.client.multiplayer.ClientPacketListener nh, BlockPos p,
			BlockHitResult bhr) {
		var b = c.level.getBlockState(p).getBlock();
		if ((b == Blocks.FARMLAND || b == Blocks.SOUL_SAND)
				&& (c.level.getBlockState(p.above()).isAir() || isHarv(c, p.above())))
			nh.send(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND,
					new BlockHitResult(bhr.getLocation(), bhr.getDirection(), p, bhr.isInside()), 0));
	}
	private void harvest(Minecraft c, net.minecraft.client.multiplayer.ClientPacketListener nh, BlockPos p,
			BlockHitResult bhr) {
		if (isHarv(c, p))
			nh.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, p,
					bhr.getDirection()));
	}
	private boolean isHarv(Minecraft c, BlockPos p) {
		var s = c.level.getBlockState(p);
		var b = s.getBlock();
		return b instanceof NetherWartBlock
				? s.getValue(NetherWartBlock.AGE) == 3
				: b instanceof CropBlock cb && cb.isMaxAge(s);
	}
}
