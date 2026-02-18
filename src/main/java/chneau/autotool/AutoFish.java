package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Items;
public class AutoFish extends BaseModule implements ClientTickEvents.EndTick {
	private static EntityDataAccessor<Boolean> DB;
	static {
		try {
			var f = FishingHook.class.getDeclaredField("DATA_BITING");
			f.setAccessible(true);
			DB = (EntityDataAccessor<Boolean>) f.get(null);
		} catch (Exception ignored) {
		}
	}
	private int recastTicks = -1;
	@Override
	public void onEndTick(Minecraft client) {
		if (config().autoFish == Config.FishMode.OFF)
			return;
		var p = client.player;
		if (!(p.getMainHandItem().is(Items.FISHING_ROD) || p.getOffhandItem().is(Items.FISHING_ROD))) {
			recastTicks = -1;
			return;
		}
		if (recastTicks > 0 && --recastTicks == 0)
			use(client);
		else if (p.fishing != null && DB != null && p.fishing.getEntityData().get(DB)) {
			use(client);
			recastTicks = 40;
		}
	}
	private void use(Minecraft c) {
		if (c.gameMode != null && c.player != null)
			c.gameMode.useItem(c.player,
					c.player.getMainHandItem().is(Items.FISHING_ROD)
							? InteractionHand.MAIN_HAND
							: InteractionHand.OFF_HAND);
	}
}
