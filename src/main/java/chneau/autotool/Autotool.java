package chneau.autotool;

import java.util.Date;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.util.hit.HitResult;

/**
 * Autotool
 */
public class Autotool implements AttackBlockCallback, AttackEntityCallback, ClientTickCallback {
    private int last = -1;
    private final Select select;
    private long lastAttack = System.currentTimeMillis();

    public Autotool(Select select) {
        this.select = select;
    }

    private void updateServer(int position) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;
        player.inventory.selectedSlot = position;
        if (player.networkHandler == null)
            return;
        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(position)); // not sure if it works
        player.tick(); // not sure if too heavy
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand h, BlockPos pos, Direction d) {
        if (last == -1)
            last = player.inventory.selectedSlot;
        BlockState bState = world.getBlockState(pos);
        int tool = select.selectTool(player.inventory, bState);
        if (tool == -1 || player.inventory.selectedSlot == tool)
            return ActionResult.PASS;
        this.updateServer(tool);
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World w, Hand h, Entity entity, EntityHitResult hr) {
        if (last == -1)
            last = player.inventory.selectedSlot;
        int sword = select.selectWeapon(player.inventory);
        if (sword == -1 || player.inventory.selectedSlot == sword)
            return ActionResult.PASS;
        last = sword;
        this.updateServer(sword);
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
            if (last != -1)
                this.updateServer(last);
            last = -1;
        } else {
            if (last == -1)
                last = inventory.selectedSlot;
        }
        if (client.hitResult == null)
            return;
        if (client.hitResult.getType() != HitResult.Type.ENTITY)
            return;
        if (player.inventory.main.get(player.inventory.selectedSlot).getItem() instanceof SwordItem == false)
            return;
        long now = System.currentTimeMillis();
        if (now - lastAttack < 625)
            return;
        client.interactionManager.attackEntity(player, ((EntityHitResult) client.hitResult).getEntity());
        player.resetLastAttackedTicks();
        player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
    }

}
