package chneau.autotool;

import net.minecraft.world.entity.ai.attributes.Attributes;

public class AutoStep extends BaseModule implements Safe.PlayerLoad {
	public AutoStep() {
		super("AutoStep");
	}

	@Override
	public void onLoad() {
		update();
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
