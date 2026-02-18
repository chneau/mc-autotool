package chneau.autotool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;

public class AutoArmor extends BaseModule implements Safe.ContainerScreenInit {
	public AutoArmor() {
		super("AutoArmor");
	}

	@Override
	public void afterInit(Minecraft client,
			net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?> screen, int w, int h) {
		if (screen instanceof InventoryScreen inv && config().autoArmor != Config.ArmorMode.OFF)
			Util.addButton(screen, inv, "A", "Equip Best Armor", 60, () -> Safe.run(name, () -> handle(client)));
	}

	private void handle(Minecraft client) {
		var p = client.player;
		if (p == null || client.gameMode == null)
			return;
		var menu = p.inventoryMenu;
		var slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
				EquipmentSlot.FEET};
		for (int i = 0; i < slots.length; i++) {
			var slot = slots[i];
			int menuIdx = 5 + i;
			var current = menu.getSlot(menuIdx).getItem();
			int bestSlot = -1;
			var best = current;
			for (int j = 9; j <= 44; j++) {
				var stack = menu.getSlot(j).getItem();
				var eq = stack.get(DataComponents.EQUIPPABLE);
				if (eq != null && eq.slot() == slot && isBetter(stack, best, slot)) {
					best = stack;
					bestSlot = j;
				}
			}
			if (bestSlot != -1) {
				Util.swap(client, menu.containerId, bestSlot, menuIdx);
				return;
			}
		}
	}

	private boolean isBetter(ItemStack n, ItemStack o, EquipmentSlot slot) {
		if (o.isEmpty())
			return true;
		double nv = Util.getArmorValue(n, slot), ov = Util.getArmorValue(o, slot);
		if (config().autoArmor == Config.ArmorMode.SMART) {
			nv += Util.getEnchantmentLevelSum(n) * 0.5;
			ov += Util.getEnchantmentLevelSum(o) * 0.5;
		}
		return nv > ov;
	}
}
