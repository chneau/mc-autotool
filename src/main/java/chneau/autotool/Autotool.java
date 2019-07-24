package chneau.autotool;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
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
    private Integer lastPosition;
    private final ISelect select;

    public Autotool(ISelect select) {
        this.select = select;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand h, BlockPos pos, Direction d) {
        if (lastPosition == null)
            lastPosition = player.inventory.selectedSlot;
        BlockState bState = world.getBlockState(pos);
        Block block = bState.getBlock();
        Item targetItem = block.asItem();
        System.out.println(targetItem.getName().asString());
        Integer selectFirstTool = select.selectTool(player.inventory, bState);
        if (selectFirstTool == null || player.inventory.selectedSlot == selectFirstTool)
            return ActionResult.PASS;
        player.inventory.selectedSlot = selectFirstTool;
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World w, Hand h, Entity entity, EntityHitResult hr) {
        if (lastPosition == null)
            lastPosition = player.inventory.selectedSlot;
        Integer selectFirstSword = select.selectWeapon(player.inventory);
        System.out.println(entity.getName().asString());
        if (selectFirstSword == null || player.inventory.selectedSlot == selectFirstSword)
            return ActionResult.PASS;
        player.inventory.selectedSlot = selectFirstSword;
        lastPosition = selectFirstSword;
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
            if (lastPosition != null)
                inventory.selectedSlot = lastPosition;
            lastPosition = null;
        } else {
            if (lastPosition == null)
                lastPosition = inventory.selectedSlot;
        }
    }

}
