package chneau.autotool;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Autotool implements AttackBlockCallback, AttackEntityCallback {
    private int last = -1;
    private final Select select;

    public Autotool(Select select) {
        this.select = select;
    }

    public void register() {
        AttackBlockCallback.EVENT.register(this);
        AttackEntityCallback.EVENT.register(this);
    }

    @Override
    public ActionResult interact(PlayerEntity p, World w, Hand h, BlockPos bp, Direction d) {
        if (h != Hand.MAIN_HAND)
            return ActionResult.PASS;
        if (last == -1)
            last = p.inventory.selectedSlot;
        BlockState bState = w.getBlockState(bp);
        int tool = select.selectTool(p.inventory, bState);
        if (tool == -1 || p.inventory.selectedSlot == tool)
            return ActionResult.PASS;
        this.updateServer(tool);
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity p, World w, Hand h, Entity e, EntityHitResult ehr) {
        if (h != Hand.MAIN_HAND)
            return ActionResult.PASS;
        if (last == -1)
            last = p.inventory.selectedSlot;
        int sword = select.selectWeapon(p.inventory);
        if (sword == -1 || p.inventory.selectedSlot == sword)
            return ActionResult.PASS;
        last = sword;
        this.updateServer(sword);
        return ActionResult.PASS;
    }

    private void updateServer(int pos) {
        MinecraftClient instance = MinecraftClient.getInstance();
        ClientPlayerEntity player = instance.player;
        if (player == null)
            return;
        player.inventory.selectedSlot = pos;
        if (player.networkHandler == null)
            return;
        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(pos));
    }
}
