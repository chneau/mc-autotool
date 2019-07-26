package chneau.autotool;

import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		(new Autotool(new SelectBest())).register();
		(new Autofarm()).register();
		(new Autoswap()).register();
		(new Autoattack()).register();
	}

}
