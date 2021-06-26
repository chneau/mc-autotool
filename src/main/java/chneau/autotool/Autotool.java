package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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
    public ActionResult interact(PlayerEntity p, World w, Hand h, BlockPos bp, Direction d) {
        if (!Util.isCurrentPlayer(p))
            return ActionResult.PASS;
        if (h != Hand.MAIN_HAND)
            return ActionResult.PASS;
        if (last == -1)
            last = p.getInventory().selectedSlot;
        BlockState bState = w.getBlockState(bp);
        int tool = select.selectTool(p.getInventory(), bState);
        if (tool == -1 || p.getInventory().selectedSlot == tool)
            return ActionResult.PASS;
        this.updateServer(tool);
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity p, World w, Hand h, Entity e, EntityHitResult ehr) {
        if (!Util.isCurrentPlayer(p))
            return ActionResult.PASS;
        if (h != Hand.MAIN_HAND)
            return ActionResult.PASS;
        if (last == -1)
            last = p.getInventory().selectedSlot;
        int sword = select.selectWeapon(p.getInventory());
        if (sword == -1 || p.getInventory().selectedSlot == sword)
            return ActionResult.PASS;
        last = sword;
        this.updateServer(sword);
        return ActionResult.PASS;
    }

    @Override
    public void onEndTick(MinecraftClient c) {
        ClientPlayerEntity p = c.player;
        if (p == null || c.crosshairTarget == null || p.getInventory() == null)
            return;
        if (!Util.isCurrentPlayer(p))
            return;
        updateLast(p.getInventory(), c.mouse.wasLeftButtonClicked());
    }

    private void updateLast(PlayerInventory i, boolean lbClicked) {
        if (lbClicked == false) {
            if (last != -1)
                this.updateServer(last);
            last = -1;
        } else {
            if (last == -1)
                last = i.selectedSlot;
        }
    }

    private void updateServer(int pos) {
        MinecraftClient instance = MinecraftClient.getInstance();
        ClientPlayerEntity p = instance.player;
        if (p == null)
            return;
        p.getInventory().selectedSlot = pos;
        if (p.networkHandler == null)
            return;
        p.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(pos));
    }
}
