package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AutoStep extends BaseModule {
	public AutoStep() {
		super("AutoStep");
	}
	@Override
	public void register() {
		ClientEntityEvents.ENTITY_LOAD.register(Safe.playerLoad(name, AutoStep::update));
	}

	public static void update() {
		var p = client().player;
		if (p == null)
			return;
		var attr = p.getAttribute(Attributes.STEP_HEIGHT);
		if (attr != null) {
			double h = config().autoStep == Config.StepMode.ON ? 1.0 : 0.6;
			if (attr.getBaseValue() != h)
				attr.setBaseValue(h);
		}
	}
}
