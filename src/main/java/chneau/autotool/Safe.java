package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
public class Safe {
	public static void run(String n, Runnable r) {
		try {
			r.run();
		} catch (Throwable t) {
			handle(n, t);
		}
	}
	public static <T> T call(String n, Supplier<T> s, T d) {
		try {
			return s.get();
		} catch (Throwable t) {
			handle(n, t);
			return d;
		}
	}
	private static void handle(String n, Throwable t) {
		Main.LOGGER.error("Error in " + n, t);
		Util.chatError("[mc-autotool] Error in " + n + ": " + t.getMessage());
	}
	public static void async(String n, Runnable r) {
		CompletableFuture.runAsync(() -> run(n, r));
	}
	public static ClientTickEvents.EndTick tick(String n, ClientTickEvents.EndTick o) {
		return c -> run(n, () -> o.onEndTick(c));
	}
	public static ClientTickEvents.EndTick playerTick(String n, ClientTickEvents.EndTick o) {
		return c -> {
			if (c.player != null && Util.isCurrentPlayer(c.player))
				tick(n, o).onEndTick(c);
		};
	}
	public interface ContainerScreenInit {
		void afterInit(Minecraft c, AbstractContainerScreen<?> s, int w, int h);
	}
	public static ScreenEvents.AfterInit containerScreen(String n, ContainerScreenInit o) {
		return (c, s, w, h) -> {
			if (s instanceof AbstractContainerScreen<?> cs)
				run(n, () -> o.afterInit(c, cs, w, h));
		};
	}
	public static UseBlockCallback use(String n, UseBlockCallback o) {
		return (p, w, h, b) -> call(n, () -> o.interact(p, w, h, b), InteractionResult.PASS);
	}
	public interface PlayerUseBlock {
		InteractionResult interact(Player p, Level w, InteractionHand h, BlockHitResult b);
	}
	public static UseBlockCallback playerUse(String n, PlayerUseBlock o) {
		return (p, w, h, b) -> Util.isCurrentPlayer(p)
				? use(n, (p2, w2, h2, b2) -> o.interact(p2, w2, h2, b2)).interact(p, w, h, b)
				: InteractionResult.PASS;
	}
	public static ClientEntityEvents.Load load(String n, ClientEntityEvents.Load o) {
		return (e, w) -> run(n, () -> o.onLoad(e, w));
	}
	public interface PlayerLoad {
		void onLoad();
	}
	public static ClientEntityEvents.Load playerLoad(String n, PlayerLoad o) {
		return (e, w) -> {
			if (Util.isCurrentPlayer(e))
				run(n, o::onLoad);
		};
	}
	public static AttackBlockCallback attackBlock(String n, AttackBlockCallback o) {
		return (p, w, h, po, d) -> call(n, () -> o.interact(p, w, h, po, d), InteractionResult.PASS);
	}
	public interface PlayerAttackBlock {
		InteractionResult interact(Player p, Level w, InteractionHand h, BlockPos po, Direction d);
	}
	public static AttackBlockCallback playerAttackBlock(String n, PlayerAttackBlock o) {
		return (p, w, h, po, d) -> Util.isCurrentPlayer(p)
				? attackBlock(n, (p2, w2, h2, po2, d2) -> o.interact(p2, w2, h2, po2, d2)).interact(p, w, h, po, d)
				: InteractionResult.PASS;
	}
	public static AttackEntityCallback attackEntity(String n, AttackEntityCallback o) {
		return (p, w, h, e, r) -> call(n, () -> o.interact(p, w, h, e, r), InteractionResult.PASS);
	}
	public interface PlayerAttackEntity {
		InteractionResult interact(Player p, Level w, InteractionHand h, Entity e, EntityHitResult r);
	}
	public static AttackEntityCallback playerAttackEntity(String n, PlayerAttackEntity o) {
		return (p, w, h, e, r) -> Util.isCurrentPlayer(p)
				? attackEntity(n, (p2, w2, h2, e2, r2) -> o.interact(p2, w2, h2, e2, r2)).interact(p, w, h, e, r)
				: InteractionResult.PASS;
	}
	public static HudRenderCallback hud(String n, HudRenderCallback o) {
		return (d, t) -> run(n, () -> o.onHudRender(d, t));
	}
}
