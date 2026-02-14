package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;

public class AutoSort implements EndTick {
    private boolean wasInventoryOpen = false;

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        var mode = ConfigManager.getConfig().autoSort;
        if (mode == Config.SortMode.OFF) return;

        boolean isInventoryOpen = client.screen instanceof InventoryScreen;
        if (isInventoryOpen && !wasInventoryOpen) {
            sortInventory(client, mode);
        }
        wasInventoryOpen = isInventoryOpen;
    }

    private void sortInventory(Minecraft client, Config.SortMode mode) {
        var player = client.player;
        if (player == null || client.gameMode == null) return;

        // Slots in InventoryMenu:
        // 9-35: Main Inventory (27 slots)
        // 36-44: Hotbar (9 slots)
        
        if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH) {
            sortSlotRange(client, 9, 35);
        }
        if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH) {
            sortSlotRange(client, 36, 44);
        }
    }

    private void sortSlotRange(Minecraft client, int start, int end) {
        var player = client.player;
        if (player == null || client.gameMode == null) return;
        var menu = player.inventoryMenu;

        for (int i = start; i <= end; i++) {
            int bestIdx = i;
            
            for (int j = i + 1; j <= end; j++) {
                if (compare(menu.getSlot(j).getItem(), menu.getSlot(bestIdx).getItem()) < 0) {
                    bestIdx = j;
                }
            }
            
            if (bestIdx != i) {
                swap(client, menu.containerId, i, bestIdx);
            }
        }
    }

    private int compare(ItemStack a, ItemStack b) {
        if (a.isEmpty() && b.isEmpty()) return 0;
        if (a.isEmpty()) return 1;
        if (b.isEmpty()) return -1;
        
        int weightA = getWeight(a);
        int weightB = getWeight(b);
        
        if (weightA != weightB) return Integer.compare(weightA, weightB);
        
        return a.getHoverName().getString().compareTo(b.getHoverName().getString());
    }

    private int getWeight(ItemStack stack) {
        if (stack.is(ItemTags.SWORDS)) return 0;
        if (stack.is(Items.BOW) || stack.is(Items.CROSSBOW)) return 1;
        if (stack.is(ItemTags.PICKAXES)) return 2;
        if (stack.is(ItemTags.AXES)) return 3;
        if (stack.is(ItemTags.SHOVELS)) return 4;
        if (stack.is(ItemTags.HOES)) return 5;
        if (stack.has(net.minecraft.core.component.DataComponents.FOOD)) return 6;
        if (stack.getItem() instanceof BlockItem) return 7;
        return 8;
    }

    private void swap(Minecraft client, int containerId, int slot1, int slot2) {
        client.gameMode.handleInventoryMouseClick(containerId, slot1, 0, ClickType.PICKUP, client.player);
        client.gameMode.handleInventoryMouseClick(containerId, slot2, 0, ClickType.PICKUP, client.player);
        client.gameMode.handleInventoryMouseClick(containerId, slot1, 0, ClickType.PICKUP, client.player);
    }
}
