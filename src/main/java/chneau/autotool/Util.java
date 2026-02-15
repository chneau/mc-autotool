package chneau.autotool;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Utility class for common Minecraft client-side operations.
 */
public class Util {
    private Util() {
    }

    /**
     * Retrieves the BlockPos of the block the player is currently looking at.
     * 
     * @param client The Minecraft client instance.
     * @return The BlockPos of the targeted block, or null if no block is targeted.
     */
    public static BlockPos getTargetedBlock(Minecraft client) {
        if (client.hitResult instanceof BlockHitResult bhr) {
            return bhr.getBlockPos();
        }
        return null;
    }

    /**
     * Checks if the given player is the local client player.
     * 
     * @param other The entity to check.
     * @return True if the entity is the local client player.
     */
    public static boolean isCurrentPlayer(net.minecraft.world.entity.Entity other) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if (player == null || other == null)
            return false;
        return player.equals(other);
    }

    public static void click(Minecraft client, int containerId, int slotId, int button, net.minecraft.world.inventory.ContainerInput type) {
        if (client.gameMode != null && client.player != null) {
            client.gameMode.handleContainerInput(containerId, slotId, button, type, client.player);
        }
    }

    public static void quickMove(Minecraft client, int containerId, int slotId) {
        click(client, containerId, slotId, 0, net.minecraft.world.inventory.ContainerInput.QUICK_MOVE);
    }

    public static void pickup(Minecraft client, int containerId, int slotId) {
        click(client, containerId, slotId, 0, net.minecraft.world.inventory.ContainerInput.PICKUP);
    }

    public static boolean areItemsEqual(net.minecraft.world.item.ItemStack a, net.minecraft.world.item.ItemStack b) {
        return net.minecraft.world.item.ItemStack.isSameItemSameComponents(a, b);
    }
}
