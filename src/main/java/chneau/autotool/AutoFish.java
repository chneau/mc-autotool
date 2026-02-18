package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Items;
import java.lang.reflect.Field;

public class AutoFish extends BaseModule implements ClientTickEvents.EndTick {
	private static final EntityDataAccessor<Boolean> DATA_BITING = fetchDataBiting();
	private int recastTicks = -1;

	public AutoFish() {
		super("AutoFish");
	}

	@SuppressWarnings("unchecked")
	private static EntityDataAccessor<Boolean> fetchDataBiting() {
		try {
			Field field = FishingHook.class.getDeclaredField("DATA_BITING");
			field.setAccessible(true);
			return (EntityDataAccessor<Boolean>) field.get(null);
		} catch (Exception e) {
			return null;
		}
	}

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
		else if (p.fishing != null && DATA_BITING != null && p.fishing.getEntityData().get(DATA_BITING)) {
			use(client);
			recastTicks = 40;
		}
	}

	private void use(Minecraft client) {
		if (client.gameMode == null || client.player == null)
			return;
		client.gameMode.useItem(client.player,
				client.player.getMainHandItem().is(Items.FISHING_ROD)
						? InteractionHand.MAIN_HAND
						: InteractionHand.OFF_HAND);
	}
}
