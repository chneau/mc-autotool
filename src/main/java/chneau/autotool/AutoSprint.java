package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
public class AutoSprint extends BaseModule implements ClientTickEvents.EndTick {
	@Override
	public void onEndTick(Minecraft client) {
		var mode = config().autoSprint;
		if (mode == Config.SprintMode.OFF)
			return;
		var p = client.player;
		if (p.horizontalCollision || p.isDescending() || p.isUsingItem())
			return;
		if (p.getFoodData().getFoodLevel() <= (mode == Config.SprintMode.HUNGER_50 ? 10 : 6)
				&& !p.getAbilities().mayfly)
			return;
		if (p.input.hasForwardImpulse() && !p.isSprinting())
			p.setSprinting(true);
	}
}
