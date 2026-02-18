package chneau.autotool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
public class AutoArmor extends BaseModule implements Safe.ContainerScreenInit {
	@Override
	public void afterInit(Minecraft c, net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?> s, int w,
			int h) {
		if (s instanceof InventoryScreen inv && config().autoArmor != Config.ArmorMode.OFF)
			Util.addButton(s, inv, "A", "Equip Best Armor", 60, () -> Safe.run(name, () -> handle(c)));
	}
	private void handle(Minecraft c) {
		var p = c.player;
		if (p == null || c.gameMode == null)
			return;
		var menu = p.inventoryMenu;
		var slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
				EquipmentSlot.FEET};
		for (int i = 0; i < slots.length; i++) {
			var slot = slots[i];
			int mIdx = 5 + i;
			var best = menu.getSlot(mIdx).getItem();
			int bSlot = -1;
			for (int j = 9; j <= 44; j++) {
				var stack = menu.getSlot(j).getItem();
				var eq = stack.get(DataComponents.EQUIPPABLE);
				if (eq != null && eq.slot() == slot && isBetter(stack, best, slot)) {
					best = stack;
					bSlot = j;
				}
			}
			if (bSlot != -1) {
				Util.swap(c, menu.containerId, bSlot, mIdx);
				return;
			}
		}
	}
	private boolean isBetter(ItemStack n, ItemStack o, EquipmentSlot s) {
		if (o.isEmpty())
			return true;
		double nv = Util.getArmorValue(n, s), ov = Util.getArmorValue(o, s);
		if (config().autoArmor == Config.ArmorMode.SMART) {
			nv += Util.getEnchantmentLevelSum(n) * 0.5;
			ov += Util.getEnchantmentLevelSum(o) * 0.5;
		}
		return nv > ov;
	}
}
