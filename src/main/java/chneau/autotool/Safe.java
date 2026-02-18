package chneau.autotool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

public class Safe {
	public static void run(String name, Runnable runnable) {
		try {
			runnable.run();
		} catch (Throwable t) {
			handle(name, t);
		}
	}

	public static <T> T call(String name, java.util.function.Supplier<T> supplier, T defaultValue) {
		try {
			return supplier.get();
		} catch (Throwable t) {
			handle(name, t);
			return defaultValue;
		}
	}

	private static void handle(String name, Throwable t) {
		Main.LOGGER.error("Exception in " + name, t);
		Util.chatError("[mc-autotool] Error in " + name + ": " + t.getMessage());
	}

	public static void async(String name, Runnable runnable) {
		CompletableFuture.runAsync(() -> run(name, runnable));
	}

	public static ClientTickEvents.EndTick tick(String name, ClientTickEvents.EndTick original) {
		return client -> run(name, () -> original.onEndTick(client));
	}

	public static ClientTickEvents.EndTick playerTick(String name, ClientTickEvents.EndTick original) {
		return client -> run(name, () -> {
			if (client.player == null || !Util.isCurrentPlayer(client.player))
				return;
			original.onEndTick(client);
		});
	}

	public static ScreenEvents.AfterInit screen(String name, ScreenEvents.AfterInit original) {
		return (client, screen, width, height) -> run(name, () -> original.afterInit(client, screen, width, height));
	}

	public interface ContainerScreenInit {
		void afterInit(Minecraft client, AbstractContainerScreen<?> screen, int width, int height);
	}

	public static ScreenEvents.AfterInit containerScreen(String name, ContainerScreenInit original) {
		return (client, screen, width, height) -> run(name, () -> {
			if (screen instanceof AbstractContainerScreen<?> containerScreen) {
				original.afterInit(client, containerScreen, width, height);
			}
		});
	}

	public static UseBlockCallback use(String name, UseBlockCallback original) {
		return (player, world, hand, bhr) -> call(name, () -> original.interact(player, world, hand, bhr),
				InteractionResult.PASS);
	}

	public interface PlayerUseBlock {
		InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult bhr);
	}

	public static UseBlockCallback playerUse(String name, PlayerUseBlock original) {
		return (player, world, hand, bhr) -> call(name, () -> {
			if (!Util.isCurrentPlayer(player))
				return InteractionResult.PASS;
			return original.interact(player, world, hand, bhr);
		}, InteractionResult.PASS);
	}

	public static ClientEntityEvents.Load load(String name, ClientEntityEvents.Load original) {
		return (entity, world) -> run(name, () -> original.onLoad(entity, world));
	}

	public static ClientEntityEvents.Load playerLoad(String name, Runnable original) {
		return (entity, world) -> run(name, () -> {
			if (Util.isCurrentPlayer(entity)) {
				original.run();
			}
		});
	}

	public static AttackBlockCallback attackBlock(String name, AttackBlockCallback original) {
		return (player, world, hand, pos, dir) -> call(name, () -> original.interact(player, world, hand, pos, dir),
				InteractionResult.PASS);
	}

	public interface PlayerAttackBlock {
		InteractionResult interact(Player player, Level world, InteractionHand hand, BlockPos pos, Direction dir);
	}

	public static AttackBlockCallback playerAttackBlock(String name, PlayerAttackBlock original) {
		return (player, world, hand, pos, dir) -> call(name, () -> {
			if (!Util.isCurrentPlayer(player))
				return InteractionResult.PASS;
			return original.interact(player, world, hand, pos, dir);
		}, InteractionResult.PASS);
	}

	public static AttackEntityCallback attackEntity(String name, AttackEntityCallback original) {
		return (player, world, hand, entity, hitResult) -> call(name,
				() -> original.interact(player, world, hand, entity, hitResult), InteractionResult.PASS);
	}

	public interface PlayerAttackEntity {
		InteractionResult interact(Player player, Level world, InteractionHand hand, Entity entity,
				EntityHitResult hitResult);
	}

	public static AttackEntityCallback playerAttackEntity(String name, PlayerAttackEntity original) {
		return (player, world, hand, entity, hitResult) -> call(name, () -> {
			if (!Util.isCurrentPlayer(player))
				return InteractionResult.PASS;
			return original.interact(player, world, hand, entity, hitResult);
		}, InteractionResult.PASS);
	}

	public static HudRenderCallback hud(String name, HudRenderCallback original) {
		return (drawContext, tickCounter) -> run(name, () -> original.onHudRender(drawContext, tickCounter));
	}
}
