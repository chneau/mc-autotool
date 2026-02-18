package chneau.autotool;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class AutoSort extends BaseModule {
	public AutoSort() {
		super("AutoSort");
	}

	@Override
	public void register() {
		ScreenEvents.AFTER_INIT.register(Safe.containerScreen(name, (client, screen, w, h) -> {
			if (config().autoSort != Config.SortMode.OFF && !(screen.getMenu() instanceof AbstractFurnaceMenu))
				Util.addButton(screen, screen, "S", "Sort Inventory", 40, () -> Safe.run(name, () -> handle(client)));
		}));
	}

	private void handle(Minecraft client) {
		var p = client.player;
		var mode = config().autoSort;
		if (p == null || client.gameMode == null)
			return;
		if (client.screen instanceof InventoryScreen) {
			if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(client, 9, 35);
			if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(client, 36, 44);
		} else {
			var menu = p.containerMenu;
			int slots = menu.slots.size(), pStart = slots - 36;
			if (pStart > 0 && mode == Config.SortMode.ALL)
				sort(client, 0, pStart - 1);
			if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(client, pStart, pStart + 26);
			if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(client, pStart + 27, slots - 1);
		}
	}

	private void sort(Minecraft client, int start, int end) {
		var menu = client.player.containerMenu;
		for (int i = start; i <= end; i++) {
			var itemI = menu.getSlot(i).getItem();
			if (itemI.isEmpty() || itemI.getCount() >= itemI.getMaxStackSize())
				continue;
			for (int j = i + 1; j <= end; j++) {
				var itemJ = menu.getSlot(j).getItem();
				if (!itemJ.isEmpty() && Util.areItemsEqual(itemI, itemJ)) {
					Util.pickup(client, menu.containerId, j);
					Util.pickup(client, menu.containerId, i);
					if (!menu.getCarried().isEmpty())
						Util.pickup(client, menu.containerId, j);
					if (menu.getSlot(i).getItem().getCount() >= itemI.getMaxStackSize())
						break;
				}
			}
		}
		List<ItemStack> target = new ArrayList<>();
		for (int i = start; i <= end; i++)
			target.add(menu.getSlot(i).getItem().copy());
		target.sort((a, b) -> a.isEmpty()
				? (b.isEmpty() ? 0 : 1)
				: (b.isEmpty()
						? -1
						: (Util.getItemWeight(a) != Util.getItemWeight(b)
								? Integer.compare(Util.getItemWeight(a), Util.getItemWeight(b))
								: a.getHoverName().getString().compareTo(b.getHoverName().getString()))));
		for (int i = 0; i < target.size(); i++) {
			int slotI = start + i;
			var goal = target.get(i);
			if (isEq(menu.getSlot(slotI).getItem(), goal))
				continue;
			Util.pickup(client, menu.containerId, slotI);
			while (!menu.getCarried().isEmpty()) {
				int tIdx = -1;
				for (int j = 0; j < target.size(); j++)
					if (isEq(menu.getCarried(), target.get(j))
							&& !isEq(menu.getSlot(start + j).getItem(), target.get(j))) {
						tIdx = j;
						break;
					}
				if (tIdx == -1) {
					Util.pickup(client, menu.containerId, slotI);
					break;
				}
				Util.pickup(client, menu.containerId, start + tIdx);
				if (tIdx == i)
					break;
			}
		}
	}
	private boolean isEq(ItemStack a, ItemStack b) {
		return a.isEmpty() ? b.isEmpty() : !b.isEmpty() && Util.areItemsEqual(a, b) && a.getCount() == b.getCount();
	}
}
