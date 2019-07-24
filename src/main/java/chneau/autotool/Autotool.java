package chneau.autotool;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Autotool
 */
public class Autotool implements AttackBlockCallback, AttackEntityCallback, ClientTickCallback {
    private int lastPosition = -1;
    private final Select select;

    public Autotool(Select select) {
        this.select = select;
    }

    private void updateServer(int position) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;
        if (player.networkHandler == null)
            return;
        player.tick();
        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(position));
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand h, BlockPos pos, Direction d) {
        if (lastPosition == -1)
            lastPosition = player.inventory.selectedSlot;
        BlockState bState = world.getBlockState(pos);
        int selectFirstTool = select.selectTool(player.inventory, bState);
        if (selectFirstTool == -1 || player.inventory.selectedSlot == selectFirstTool)
            return ActionResult.PASS;
        player.inventory.selectedSlot = selectFirstTool;
        this.updateServer(selectFirstTool);
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World w, Hand h, Entity entity, EntityHitResult hr) {
        if (lastPosition == -1)
            lastPosition = player.inventory.selectedSlot;
        int selectFirstSword = select.selectWeapon(player.inventory);
        if (selectFirstSword == -1 || player.inventory.selectedSlot == selectFirstSword)
            return ActionResult.PASS;
        player.inventory.selectedSlot = selectFirstSword;
        lastPosition = selectFirstSword;
        this.updateServer(selectFirstSword);
        return ActionResult.PASS;
    }

    @Override
    public void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || player.inventory == null)
            return;
        PlayerInventory inventory = player.inventory;
        boolean wasLeftButtonClicked = client.mouse.wasLeftButtonClicked();
        if (wasLeftButtonClicked == false) {
            if (lastPosition != -1) {
                inventory.selectedSlot = lastPosition;
                this.updateServer(lastPosition);
            }
            lastPosition = -1;
        } else {
            if (lastPosition == -1)
                lastPosition = inventory.selectedSlot;
        }
    }

}
