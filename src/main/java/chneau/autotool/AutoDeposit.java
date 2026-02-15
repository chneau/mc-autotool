package chneau.autotool;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoDeposit {
	private static Field leftPosField;
	private static Field topPosField;
	private static Field imageWidthField;

	static {
		try {
			leftPosField = AbstractContainerScreen.class.getDeclaredField("leftPos");
			leftPosField.setAccessible(true);
			topPosField = AbstractContainerScreen.class.getDeclaredField("topPos");
			topPosField.setAccessible(true);
			imageWidthField = AbstractContainerScreen.class.getDeclaredField("imageWidth");
			imageWidthField.setAccessible(true);
		} catch (Exception e) {
			Main.LOGGER.error("Failed to access AbstractContainerScreen fields", e);
		}
	}

	public void register() {
		ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
			if (screen instanceof AbstractContainerScreen<?> containerScreen) {
				setupButton(client, containerScreen, screen);
			}
		});
	}

	private void setupButton(Minecraft client, AbstractContainerScreen<?> containerScreen, Screen screen) {
		var mode = ConfigManager.getConfig().autoDeposit;
		if (mode == Config.DepositMode.OFF)
			return;

		var menu = containerScreen.getMenu();
		boolean isFurnace = menu instanceof AbstractFurnaceMenu;

		if (isFurnace) {
			if (mode != Config.DepositMode.FURNACE && mode != Config.DepositMode.ALL)
				return;
		} else {
			if (mode != Config.DepositMode.CHEST && mode != Config.DepositMode.ALL)
				return;
		}

		try {
			int leftPos = leftPosField.getInt(containerScreen);
			int topPos = topPosField.getInt(containerScreen);
			int imageWidth = imageWidthField.getInt(containerScreen);

			// Position button at the top right of the container area
			Button depositButton = Button.builder(Component.literal("D"), (btn) -> {
				handleDeposit(client, containerScreen, isFurnace);
			}).bounds(leftPos + imageWidth - 20, topPos + 5, 15, 15).build();

			Screens.getWidgets(screen).add(depositButton);
		} catch (Exception e) {
			Main.LOGGER.error("Failed to add deposit button", e);
		}
	}

	private void handleDeposit(Minecraft client, AbstractContainerScreen<?> screen, boolean isFurnace) {
		var menu = screen.getMenu();
		if (isFurnace) {
			fillContainer(client, (AbstractContainerMenu) menu, false);
		} else {
			fillContainer(client, (AbstractContainerMenu) menu, true);
		}
	}

	private void fillContainer(Minecraft client, AbstractContainerMenu menu, boolean smart) {
		Set<Item> existingItems = new HashSet<>();
		List<Integer> playerSlots = new ArrayList<>();

		for (int i = 0; i < menu.slots.size(); i++) {
			Slot slot = menu.getSlot(i);
			if (slot.container instanceof Inventory) {
				playerSlots.add(i);
			} else {
				ItemStack stack = slot.getItem();
				if (!stack.isEmpty()) {
					existingItems.add(stack.getItem());
				}
			}
		}

		for (int slotId : playerSlots) {
			ItemStack playerStack = menu.getSlot(slotId).getItem();
			if (playerStack.isEmpty())
				continue;

			if (smart && !existingItems.contains(playerStack.getItem())) {
				continue;
			}

			Util.quickMove(client, menu.containerId, slotId);
		}
	}
}
