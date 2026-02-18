package chneau.autotool;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
public class AutoArmor {
	public void register() {
		ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
			if (screen instanceof InventoryScreen inventoryScreen) {
				setupButton(client, inventoryScreen, screen);
			}
		});
	}
	private void setupButton(Minecraft client, AbstractContainerScreen<?> containerScreen, Screen screen) {
		var mode = ConfigManager.getConfig().autoArmor;
		if (mode == Config.ArmorMode.OFF)
			return;
		Util.addButton(screen, containerScreen, "A", "Equip Best Armor", 60, () -> handleAutoArmor(client, mode));
	}
	private void handleAutoArmor(Minecraft client, Config.ArmorMode mode) {
		var player = client.player;
		if (player == null || client.gameMode == null)
			return;
		var menu = player.inventoryMenu;
		// Armor slots in InventoryMenu: 5 (HEAD), 6 (CHEST), 7 (LEGS), 8 (FEET)
		for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
				EquipmentSlot.FEET}) {
			int menuIdx = switch (slot) {
				case HEAD -> 5;
				case CHEST -> 6;
				case LEGS -> 7;
				case FEET -> 8;
				default -> -1;
			};
			if (menuIdx == -1)
				continue;
			ItemStack currentArmor = menu.getSlot(menuIdx).getItem();
			int bestSlot = -1;
			ItemStack bestArmor = currentArmor;
			// Search in inventory (9-44)
			for (int i = 9; i <= 44; i++) {
				ItemStack stack = menu.getSlot(i).getItem();
				var equippable = stack.get(DataComponents.EQUIPPABLE);
				if (equippable != null && equippable.slot() == slot) {
					if (isBetter(stack, bestArmor, slot, mode == Config.ArmorMode.SMART)) {
						bestArmor = stack;
						bestSlot = i;
					}
				}
			}
			if (bestSlot != -1) {
				equip(client, menu.containerId, bestSlot, menuIdx);
				return; // Only one swap per tick
			}
		}
	}
	private boolean isBetter(ItemStack newStack, ItemStack oldStack, EquipmentSlot slot, boolean smart) {
		if (oldStack.isEmpty())
			return true;
		double newValue = Util.getArmorValue(newStack, slot);
		double oldValue = Util.getArmorValue(oldStack, slot);
		if (smart) {
			newValue += Util.getEnchantmentLevelSum(newStack) * 0.5;
			oldValue += Util.getEnchantmentLevelSum(oldStack) * 0.5;
		}
		return newValue > oldValue;
	}
	private void equip(Minecraft client, int containerId, int inventorySlot, int armorSlot) {
		Util.swap(client, containerId, inventorySlot, armorSlot);
	}
}
