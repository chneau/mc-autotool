package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Autotool implements AttackBlockCallback, AttackEntityCallback, EndTick {
    private int last = -1;
    private final Select select;

    public Autotool(Select select) {
        this.select = select;
    }

    public void register() {
        AttackBlockCallback.EVENT.register(this);
        AttackEntityCallback.EVENT.register(this);
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos blockPos, Direction direction) {
        if (!Util.isCurrentPlayer(player))
            return ActionResult.PASS;
        if (hand != Hand.MAIN_HAND)
            return ActionResult.PASS;
        if (last == -1)
            last = player.getInventory().selectedSlot;
        var bState = world.getBlockState(blockPos);
        var tool = select.selectTool(player.getInventory(), bState);
        if (tool == -1 || player.getInventory().selectedSlot == tool)
            return ActionResult.PASS;
        updateServer(tool);
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult ehr) {
        if (!Util.isCurrentPlayer(player))
            return ActionResult.PASS;
        if (hand != Hand.MAIN_HAND)
            return ActionResult.PASS;
        if (last == -1)
            last = player.getInventory().selectedSlot;
        var sword = select.selectWeapon(player.getInventory());
        if (sword == -1 || player.getInventory().selectedSlot == sword)
            return ActionResult.PASS;
        last = sword;
        updateServer(sword);
        return ActionResult.PASS;
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        var player = client.player;
        if (player == null || client.crosshairTarget == null || player.getInventory() == null)
            return;
        if (!Util.isCurrentPlayer(player))
            return;
        updateLast(player.getInventory(), client.mouse.wasLeftButtonClicked());
    }

    private void updateLast(PlayerInventory inventory, boolean lbClicked) {
        if (!lbClicked) {
            if (last != -1)
                this.updateServer(last);
            last = -1;
        } else if (last == -1) {
            last = inventory.selectedSlot;
        }
    }

    private void updateServer(int pos) {
        var instance = MinecraftClient.getInstance();
        var player = instance.player;
        if (player == null)
            return;
        player.getInventory().selectedSlot = pos;
        if (player.networkHandler == null)
            return;
        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(pos));
    }
}
