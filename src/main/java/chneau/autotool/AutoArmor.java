package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;

public class AutoArmor implements EndTick {
    private int lastInventoryChangeCount = -1;

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        var mode = ConfigManager.getConfig().autoArmor;
        if (mode == Config.ArmorMode.OFF) return;

        var player = client.player;
        if (player == null || client.gameMode == null) return;

        int currentChangeCount = player.getInventory().getTimesChanged();

        // If inventory hasn't changed, only check once every 20 ticks (1 second)
        if (currentChangeCount == lastInventoryChangeCount) {
            if (!Throttler.shouldRun(this, 20)) {
                return;
            }
        }
        
        lastInventoryChangeCount = currentChangeCount;

        var menu = player.inventoryMenu;
        
        // Armor slots in InventoryMenu: 5 (HEAD), 6 (CHEST), 7 (LEGS), 8 (FEET)
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            int menuIdx = switch (slot) {
                case HEAD -> 5;
                case CHEST -> 6;
                case LEGS -> 7;
                case FEET -> 8;
                default -> -1;
            };
            if (menuIdx == -1) continue;

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
        if (oldStack.isEmpty()) return true;

        double newValue = getArmorValue(newStack, slot);
        double oldValue = getArmorValue(oldStack, slot);

        if (smart) {
            var newEnchants = newStack.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
            var oldEnchants = oldStack.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
            
            int newLevels = newEnchants.keySet().stream().mapToInt(newEnchants::getLevel).sum();
            int oldLevels = oldEnchants.keySet().stream().mapToInt(oldEnchants::getLevel).sum();
            
            newValue += newLevels * 0.5;
            oldValue += oldLevels * 0.5;
        }

        return newValue > oldValue;
    }

    private double getArmorValue(ItemStack stack, EquipmentSlot slot) {
        var modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        double armor = modifiers.compute(Attributes.ARMOR, 0.0, slot);
        double toughness = modifiers.compute(Attributes.ARMOR_TOUGHNESS, 0.0, slot);
        return armor + toughness;
    }

    private void equip(Minecraft client, int containerId, int inventorySlot, int armorSlot) {
        if (client.player.inventoryMenu.getSlot(armorSlot).getItem().isEmpty()) {
            Util.quickMove(client, containerId, inventorySlot);
        } else {
            Util.pickup(client, containerId, inventorySlot);
            Util.pickup(client, containerId, armorSlot);
            Util.pickup(client, containerId, inventorySlot);
        }
    }
}
