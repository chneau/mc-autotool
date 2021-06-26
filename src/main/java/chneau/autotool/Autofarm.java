package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class Autofarm implements EndTick {

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(MinecraftClient c) {
        ClientPlayerEntity p = c.player;
        if (p == null || c.crosshairTarget == null || p.getInventory() == null)
            return;
        if (!Util.isCurrentPlayer(p))
            return;
        PlayerInventory inventory = p.getInventory();
        Item itemMainHand = inventory.main.get(inventory.selectedSlot).getItem();
        if (c.crosshairTarget.getType() == Type.BLOCK) {
            boolean isSeed = itemMainHand instanceof AliasedBlockItem;
            boolean isTool = itemMainHand instanceof MiningToolItem;
            if (!(isSeed || isTool))
                return;
            ClientPlayNetworkHandler networkHandler = c.getNetworkHandler();
            if (networkHandler == null)
                return;
            BlockPos blockPos = Util.getTargetedBlock(c);
            BlockHitResult bhr = (BlockHitResult) c.crosshairTarget;
            harvest(c, networkHandler, blockPos, bhr);
            harvest(c, networkHandler, blockPos.east(), bhr);
            harvest(c, networkHandler, blockPos.east().north(), bhr);
            harvest(c, networkHandler, blockPos.west(), bhr);
            harvest(c, networkHandler, blockPos.west().south(), bhr);
            harvest(c, networkHandler, blockPos.south(), bhr);
            harvest(c, networkHandler, blockPos.south().east(), bhr);
            harvest(c, networkHandler, blockPos.north(), bhr);
            harvest(c, networkHandler, blockPos.north().west(), bhr);
            if (isSeed) {
                BlockPos bp = bhr.getBlockPos();
                plant(c, networkHandler, bp, bhr);
                plant(c, networkHandler, bp.east(), bhr);
                plant(c, networkHandler, bp.east().north(), bhr);
                plant(c, networkHandler, bp.west(), bhr);
                plant(c, networkHandler, bp.west().south(), bhr);
                plant(c, networkHandler, bp.south(), bhr);
                plant(c, networkHandler, bp.south().east(), bhr);
                plant(c, networkHandler, bp.north(), bhr);
                plant(c, networkHandler, bp.north().west(), bhr);
                bp = bp.down();
                plant(c, networkHandler, bp, bhr);
                plant(c, networkHandler, bp.east(), bhr);
                plant(c, networkHandler, bp.east().north(), bhr);
                plant(c, networkHandler, bp.west(), bhr);
                plant(c, networkHandler, bp.west().south(), bhr);
                plant(c, networkHandler, bp.south(), bhr);
                plant(c, networkHandler, bp.south().east(), bhr);
                plant(c, networkHandler, bp.north(), bhr);
                plant(c, networkHandler, bp.north().west(), bhr);
            }
        }
    }

    private void plant(MinecraftClient c, ClientPlayNetworkHandler networkHandler, BlockPos blockPos,
            BlockHitResult bhr) {
        Block above = c.world.getBlockState(blockPos.up()).getBlock();
        if (!(above.equals(Blocks.AIR) || checkBlockIsHarvestable(c, blockPos.up())))
            return;
        Block block = c.world.getBlockState(blockPos).getBlock();
        if (!(block.equals(Blocks.FARMLAND) || block.equals(Blocks.SOUL_SAND)))
            return;
        networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
                new BlockHitResult(bhr.getPos(), bhr.getSide(), blockPos, bhr.isInsideBlock())));
    }

    private void harvest(MinecraftClient c, ClientPlayNetworkHandler networkHandler, BlockPos blockPos,
            BlockHitResult bhr) {
        if (!checkBlockIsHarvestable(c, blockPos))
            return;
        networkHandler.sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, bhr.getSide()));
    }

    private boolean checkBlockIsHarvestable(MinecraftClient c, BlockPos blockPos) {
        BlockState state = c.world.getBlockState(blockPos);
        Block block = state.getBlock();
        int maxAge = 0;
        int age = 0;
        if (block instanceof NetherWartBlock) {
            maxAge = 3;
            age = state.get(NetherWartBlock.AGE);
        } else if (block instanceof CropBlock) {
            CropBlock cropBlock = (CropBlock) block;
            maxAge = cropBlock.getMaxAge();
            age = state.get(cropBlock.getAgeProperty());
        } else
            return false;
        if (age != maxAge)
            return false;
        return true;
    }

}
