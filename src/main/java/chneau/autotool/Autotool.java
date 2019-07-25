package chneau.autotool;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Autotool
 */
public class Autotool implements AttackBlockCallback, AttackEntityCallback, ClientTickCallback {
    private int last = -1;
    private final Select select;
    private long lastAttack = System.currentTimeMillis();
    private final static double wee = 1e-6;

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
        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(position));
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
        updateLast(inventory, client.mouse.wasLeftButtonClicked());
        if (client.hitResult == null)
            return;
        Item itemMainHand = player.inventory.main.get(player.inventory.selectedSlot).getItem();
        switch (client.hitResult.getType()) {
        case ENTITY:
            if (itemMainHand instanceof SwordItem == false)
                return;
            long now = System.currentTimeMillis();
            if (now - lastAttack < 625)
                return;
            client.interactionManager.attackEntity(player, ((EntityHitResult) client.hitResult).getEntity());
            player.resetLastAttackedTicks();
            player.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
            break;
        case BLOCK:
            if (itemMainHand instanceof AliasedBlockItem == false)
                return;
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            if (networkHandler == null)
                return;
            BlockPos blockPos = getTargetedBlock(client);
            BlockState state = client.world.getBlockState(blockPos);
            Block block = state.getBlock();
            BlockHitResult bhr = (BlockHitResult) client.hitResult;
            if (block == Blocks.FARMLAND || block == Blocks.SOUL_SAND) {
                networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr));
                player.swingHand(Hand.MAIN_HAND);
                return;
            }
            if (block instanceof CropBlock == false)
                return;
            CropBlock cropBlock = (CropBlock) block;
            int maxAge = cropBlock.getMaxAge();
            int age = state.get(cropBlock.getAgeProperty());
            if (age != maxAge)
                return;
            networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                    blockPos, bhr.getSide()));
            player.swingHand(Hand.MAIN_HAND);
        default:
            break;
        }
    }

    private BlockPos getTargetedBlock(MinecraftClient client) {
        Vec3d cameraPos = client.cameraEntity.getCameraPosVec(1);
        Vec3d pos = client.hitResult.getPos();
        double x = (pos.x - cameraPos.x > 0) ? wee : -wee;
        double y = (pos.y - cameraPos.y > 0) ? wee : -wee;
        double z = (pos.z - cameraPos.z > 0) ? wee : -wee;
        pos = pos.add(x, y, z);
        BlockPos blockPos = new BlockPos(pos);
        return blockPos;
    }

    private void updateLast(PlayerInventory inventory, boolean wasLeftButtonClicked) {
        if (wasLeftButtonClicked == false) {
            if (last != -1)
                this.updateServer(last);
            last = -1;
        } else {
            if (last == -1)
                last = inventory.selectedSlot;
        }
    }

}
