package chneau.autotool;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;

public class Main implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		Autotool autotool = new Autotool(new SelectFirst());
		AttackBlockCallback.EVENT.register(autotool);
		AttackEntityCallback.EVENT.register(autotool);
		ClientTickCallback.EVENT.register(autotool);
	}

}
