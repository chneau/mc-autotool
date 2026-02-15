package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;

import java.util.ArrayList;
import java.util.List;

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

        if (mode == Config.SortMode.INVENTORY || mode == Config.SortMode.BOTH) {
            sortSlotRange(client, 9, 35);
        }
        if (mode == Config.SortMode.HOTBAR || mode == Config.SortMode.BOTH) {
            sortSlotRange(client, 36, 44);
        }
    }

    /**
     * Uses an optimized Cycle Sort variation to minimize item movements (packets).
     */
    private void sortSlotRange(Minecraft client, int start, int end) {
        var player = client.player;
        var menu = player.inventoryMenu;

        List<ItemStack> targetOrder = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            targetOrder.add(menu.getSlot(i).getItem().copy());
        }
        targetOrder.sort(this::compare);

        for (int i = 0; i < targetOrder.size(); i++) {
            int slotI = start + i;
            ItemStack currentI = menu.getSlot(slotI).getItem();
            ItemStack goalI = targetOrder.get(i);

            if (isEqual(currentI, goalI)) continue;

            // Start a cycle
            click(client, menu.containerId, slotI, ContainerInput.PICKUP);
            
            int currentHoleIdx = i;
            while (true) {
                ItemStack held = player.containerMenu.getCarried();
                if (held.isEmpty()) break;

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
                    click(client, menu.containerId, start + currentHoleIdx, ContainerInput.PICKUP);
                    break;
                }

                click(client, menu.containerId, start + targetIdx, ContainerInput.PICKUP);
                if (targetIdx == i) break; // Cycle complete
                currentHoleIdx = targetIdx;
            }
        }
    }

    private boolean isEqual(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) return a.isEmpty() == b.isEmpty();
        return ItemStack.isSameItemSameComponents(a, b) && a.getCount() == b.getCount();
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

    private void click(Minecraft client, int containerId, int slotId, ContainerInput type) {
        client.gameMode.handleContainerInput(containerId, slotId, 0, type, client.player);
    }
}
