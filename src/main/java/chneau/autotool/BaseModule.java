package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
public abstract class BaseModule implements Module {
	protected final String name;
	protected BaseModule() {
		this.name = getClass().getSimpleName();
	}
	protected static Config config() {
		return ConfigManager.getConfig();
	}
	protected static Minecraft client() {
		return Minecraft.getInstance();
	}
	@Override
	public void register() {
		if (this instanceof ClientTickEvents.EndTick m)
			ClientTickEvents.END_CLIENT_TICK.register(Safe.playerTick(name, m));
		if (this instanceof AttackBlockCallback m)
			AttackBlockCallback.EVENT.register(Safe.attackBlock(name, m));
		if (this instanceof Safe.PlayerAttackBlock m)
			AttackBlockCallback.EVENT.register(Safe.playerAttackBlock(name, m));
		if (this instanceof AttackEntityCallback m)
			AttackEntityCallback.EVENT.register(Safe.attackEntity(name, m));
		if (this instanceof Safe.PlayerAttackEntity m)
			AttackEntityCallback.EVENT.register(Safe.playerAttackEntity(name, m));
		if (this instanceof UseBlockCallback m)
			UseBlockCallback.EVENT.register(Safe.use(name, m));
		if (this instanceof Safe.PlayerUseBlock m)
			UseBlockCallback.EVENT.register(Safe.playerUse(name, m));
		if (this instanceof ClientEntityEvents.Load m)
			ClientEntityEvents.ENTITY_LOAD.register(Safe.load(name, m));
		if (this instanceof Safe.PlayerLoad m)
			ClientEntityEvents.ENTITY_LOAD.register(Safe.playerLoad(name, m));
		if (this instanceof Safe.ContainerScreenInit m)
			ScreenEvents.AFTER_INIT.register(Safe.containerScreen(name, m));
		if (this instanceof HudRenderCallback m)
			HudRenderCallback.EVENT.register(Safe.hud(name, m));
	}
}
