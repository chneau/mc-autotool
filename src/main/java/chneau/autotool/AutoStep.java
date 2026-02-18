package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attributes;
public class AutoStep {
	public void register() {
		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> Safe.run("AutoStep", () -> {
			if (Util.isCurrentPlayer(entity)) {
				update();
			}
		}));
	}
	public static void update() {
		Minecraft client = Minecraft.getInstance();
		var player = client.player;
		if (player == null)
			return;
		var mode = ConfigManager.getConfig().autoStep;
		var stepHeightAttr = player.getAttribute(Attributes.STEP_HEIGHT);
		if (stepHeightAttr != null) {
			double targetHeight = mode == Config.StepMode.ON ? 1.0 : 0.6;
			if (stepHeightAttr.getBaseValue() != targetHeight) {
				stepHeightAttr.setBaseValue(targetHeight);
			}
		}
	}
}
