package chneau.autotool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import java.util.*;
public class AutoSort extends BaseModule implements Safe.ContainerScreenInit {
	@Override
	public void afterInit(Minecraft c, net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?> s, int w,
			int h) {
		if (config().autoSort != Config.SortMode.OFF && !(s.getMenu() instanceof AbstractFurnaceMenu))
			Util.addButton(s, s, "S", "Sort Inventory", 40, () -> Safe.run(name, () -> handle(c)));
	}
	private void handle(Minecraft c) {
		var p = c.player;
		var mode = config().autoSort;
		if (p == null || c.gameMode == null)
			return;
		if (c.screen instanceof InventoryScreen) {
			if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(c, 9, 35);
			if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(c, 36, 44);
		} else {
			var m = p.containerMenu;
			int s = m.slots.size(), ps = s - 36;
			if (ps > 0 && mode == Config.SortMode.ALL)
				sort(c, 0, ps - 1);
			if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(c, ps, ps + 26);
			if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH || mode == Config.SortMode.ALL)
				sort(c, ps + 27, s - 1);
		}
	}
	private void sort(Minecraft c, int s, int e) {
		var m = c.player.containerMenu;
		for (int i = s; i <= e; i++) {
			var itI = m.getSlot(i).getItem();
			if (itI.isEmpty() || itI.getCount() >= itI.getMaxStackSize())
				continue;
			for (int j = i + 1; j <= e; j++) {
				var itJ = m.getSlot(j).getItem();
				if (!itJ.isEmpty() && Util.areItemsEqual(itI, itJ)) {
					Util.pickup(c, m.containerId, j);
					Util.pickup(c, m.containerId, i);
					if (!m.getCarried().isEmpty())
						Util.pickup(c, m.containerId, j);
					if (m.getSlot(i).getItem().getCount() >= itI.getMaxStackSize())
						break;
				}
			}
		}
		List<ItemStack> target = new ArrayList<>();
		for (int i = s; i <= e; i++)
			target.add(m.getSlot(i).getItem().copy());
		target.sort((a, b) -> a.isEmpty()
				? (b.isEmpty() ? 0 : 1)
				: (b.isEmpty()
						? -1
						: (Util.getItemWeight(a) != Util.getItemWeight(b)
								? Integer.compare(Util.getItemWeight(a), Util.getItemWeight(b))
								: a.getHoverName().getString().compareTo(b.getHoverName().getString()))));
		for (int i = 0; i < target.size(); i++) {
			int slotI = s + i;
			var goal = target.get(i);
			if (isEq(m.getSlot(slotI).getItem(), goal))
				continue;
			Util.pickup(c, m.containerId, slotI);
			while (!m.getCarried().isEmpty()) {
				int tIdx = -1;
				for (int j = 0; j < target.size(); j++)
					if (isEq(m.getCarried(), target.get(j)) && !isEq(m.getSlot(s + j).getItem(), target.get(j))) {
						tIdx = j;
						break;
					}
				if (tIdx == -1) {
					Util.pickup(c, m.containerId, slotI);
					break;
				}
				Util.pickup(c, m.containerId, s + tIdx);
				if (tIdx == i)
					break;
			}
		}
	}
	private boolean isEq(ItemStack a, ItemStack b) {
		return a.isEmpty() ? b.isEmpty() : !b.isEmpty() && Util.areItemsEqual(a, b) && a.getCount() == b.getCount();
	}
}
