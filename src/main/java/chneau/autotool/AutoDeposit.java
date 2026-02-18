package chneau.autotool;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class AutoDeposit {
	public void register() {
		ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> Safe.run("AutoDeposit.ScreenInit", () -> {
			if (screen instanceof AbstractContainerScreen<?> containerScreen) {
				setupButton(client, containerScreen, screen);
			}
		}));
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
		Util.addButton(screen, containerScreen, "D", "Deposit Items", 20,
				() -> Safe.run("AutoDeposit.handleDeposit", () -> handleDeposit(client, containerScreen, isFurnace)));
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
