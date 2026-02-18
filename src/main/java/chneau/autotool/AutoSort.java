package chneau.autotool;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
public class AutoSort {
	public void register() {
		ScreenEvents.AFTER_INIT.register(Safe.screen("AutoSort.ScreenInit", (client, screen, width, height) -> {
			if (screen instanceof AbstractContainerScreen<?> containerScreen) {
				setupButton(client, containerScreen, screen);
			}
		}));
	}

	private void setupButton(Minecraft client, AbstractContainerScreen<?> containerScreen, Screen screen) {
		var mode = ConfigManager.getConfig().autoSort;
		if (mode == Config.SortMode.OFF || containerScreen.getMenu() instanceof AbstractFurnaceMenu)
			return;
		Util.addButton(screen, containerScreen, "S", "Sort Inventory", 40,
				() -> Safe.run("AutoSort.sortInventory", () -> sortInventory(client, mode)));
	}
	private void sortInventory(Minecraft client, Config.SortMode mode) {
		var player = client.player;
		if (player == null || client.gameMode == null)
			return;
		if (client.screen instanceof InventoryScreen) {
			if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL) {
				sortSlotRange(client, 9, 35);
			}
			if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL) {
				sortSlotRange(client, 36, 44);
			}
		} else {
			var menu = player.containerMenu;
			int slots = menu.slots.size();
			int playerStart = slots - 36;
			if (playerStart > 0 && mode == Config.SortMode.ALL) {
				sortSlotRange(client, 0, playerStart - 1);
			}
			if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL) {
				sortSlotRange(client, playerStart, playerStart + 26);
			}
			if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL) {
				sortSlotRange(client, playerStart + 27, slots - 1);
			}
		}
	}
	/**
	 * Uses an optimized Cycle Sort variation to minimize item movements (packets).
	 */
	private void sortSlotRange(Minecraft client, int start, int end) {
		consolidateStacks(client, start, end);
		var player = client.player;
		var menu = player.containerMenu;
		List<ItemStack> targetOrder = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			targetOrder.add(menu.getSlot(i).getItem().copy());
		}
		targetOrder.sort(this::compare);
		for (int i = 0; i < targetOrder.size(); i++) {
			int slotI = start + i;
			ItemStack currentI = menu.getSlot(slotI).getItem();
			ItemStack goalI = targetOrder.get(i);
			if (isEqual(currentI, goalI))
				continue;
			// Start a cycle
			Util.pickup(client, menu.containerId, slotI);
			int currentHoleIdx = i;
			while (true) {
				ItemStack held = player.containerMenu.getCarried();
				if (held.isEmpty())
					break;
				// Find where 'held' should go
				int targetIdx = -1;
				for (int j = 0; j < targetOrder.size(); j++) {
					ItemStack targetGoal = targetOrder.get(j);
					ItemStack targetCurrent = menu.getSlot(start + j).getItem();
					if (isEqual(held, targetGoal) && !isEqual(targetCurrent, targetGoal)) {
						targetIdx = j;
						break;
					}
				}
				if (targetIdx == -1) {
					// Should not happen, but as a fallback, put it in the current hole
					Util.pickup(client, menu.containerId, start + currentHoleIdx);
					break;
				}
				Util.pickup(client, menu.containerId, start + targetIdx);
				if (targetIdx == i)
					break; // Cycle complete
				currentHoleIdx = targetIdx;
			}
		}
	}
	private boolean isEqual(ItemStack a, ItemStack b) {
		if (a.isEmpty() || b.isEmpty())
			return a.isEmpty() == b.isEmpty();
		return Util.areItemsEqual(a, b) && a.getCount() == b.getCount();
	}
	private int compare(ItemStack a, ItemStack b) {
		if (a.isEmpty() && b.isEmpty())
			return 0;
		if (a.isEmpty())
			return 1;
		if (b.isEmpty())
			return -1;
		int weightA = Util.getItemWeight(a);
		int weightB = Util.getItemWeight(b);
		if (weightA != weightB)
			return Integer.compare(weightA, weightB);
		return a.getHoverName().getString().compareTo(b.getHoverName().getString());
	}
	private void consolidateStacks(Minecraft client, int start, int end) {
		var player = client.player;
		var menu = player.containerMenu;
		for (int i = start; i <= end; i++) {
			ItemStack itemI = menu.getSlot(i).getItem();
			if (itemI.isEmpty() || itemI.getCount() >= itemI.getMaxStackSize())
				continue;
			for (int j = i + 1; j <= end; j++) {
				ItemStack itemJ = menu.getSlot(j).getItem();
				if (itemJ.isEmpty())
					continue;
				if (Util.areItemsEqual(itemI, itemJ)) {
					Util.pickup(client, menu.containerId, j);
					Util.pickup(client, menu.containerId, i);
					if (!player.containerMenu.getCarried().isEmpty()) {
						Util.pickup(client, menu.containerId, j);
					}
					itemI = menu.getSlot(i).getItem();
					if (itemI.getCount() >= itemI.getMaxStackSize()) {
						break;
					}
				}
			}
		}
	}
}
