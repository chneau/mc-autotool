package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.AliasedBlockItem;
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
    public void onEndTick(MinecraftClient client) {
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player))
            return;
        if (client.crosshairTarget == null || player.getInventory() == null)
            return;
        var inventory = player.getInventory();
        var itemMainHand = inventory.main.get(inventory.selectedSlot).getItem();
        if (client.crosshairTarget.getType() == Type.BLOCK) {
            var isSeed = itemMainHand instanceof AliasedBlockItem;
            var isTool = itemMainHand instanceof MiningToolItem;
            if (!(isSeed || isTool))
                return;
            var networkHandler = client.getNetworkHandler();
            if (networkHandler == null)
                return;
            var blockPos = Util.getTargetedBlock(client);
            var bhr = (BlockHitResult) client.crosshairTarget;
            harvest(client, networkHandler, blockPos, bhr);
            harvest(client, networkHandler, blockPos.east(), bhr);
            harvest(client, networkHandler, blockPos.east().north(), bhr);
            harvest(client, networkHandler, blockPos.west(), bhr);
            harvest(client, networkHandler, blockPos.west().south(), bhr);
            harvest(client, networkHandler, blockPos.south(), bhr);
            harvest(client, networkHandler, blockPos.south().east(), bhr);
            harvest(client, networkHandler, blockPos.north(), bhr);
            harvest(client, networkHandler, blockPos.north().west(), bhr);
            if (isSeed) {
                blockPos = bhr.getBlockPos();
                plant(client, networkHandler, blockPos, bhr);
                plant(client, networkHandler, blockPos.east(), bhr);
                plant(client, networkHandler, blockPos.east().north(), bhr);
                plant(client, networkHandler, blockPos.west(), bhr);
                plant(client, networkHandler, blockPos.west().south(), bhr);
                plant(client, networkHandler, blockPos.south(), bhr);
                plant(client, networkHandler, blockPos.south().east(), bhr);
                plant(client, networkHandler, blockPos.north(), bhr);
                plant(client, networkHandler, blockPos.north().west(), bhr);
                blockPos = blockPos.down();
                plant(client, networkHandler, blockPos, bhr);
                plant(client, networkHandler, blockPos.east(), bhr);
                plant(client, networkHandler, blockPos.east().north(), bhr);
                plant(client, networkHandler, blockPos.west(), bhr);
                plant(client, networkHandler, blockPos.west().south(), bhr);
                plant(client, networkHandler, blockPos.south(), bhr);
                plant(client, networkHandler, blockPos.south().east(), bhr);
                plant(client, networkHandler, blockPos.north(), bhr);
                plant(client, networkHandler, blockPos.north().west(), bhr);
            }
        }
    }

    private void plant(MinecraftClient client, ClientPlayNetworkHandler nh, BlockPos blockPos, BlockHitResult bhr) {
        var above = client.world.getBlockState(blockPos.up()).getBlock();
        if (!(above.equals(Blocks.AIR) || checkBlockIsHarvestable(client, blockPos.up())))
            return;
        var block = client.world.getBlockState(blockPos).getBlock();
        if (!(block.equals(Blocks.FARMLAND) || block.equals(Blocks.SOUL_SAND)))
            return;
        bhr = new BlockHitResult(bhr.getPos(), bhr.getSide(), blockPos, bhr.isInsideBlock());
        nh.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr, 0));
    }

    private void harvest(MinecraftClient client, ClientPlayNetworkHandler networkHandler, BlockPos blockPos,
            BlockHitResult bhr) {
        if (!checkBlockIsHarvestable(client, blockPos))
            return;
        var action = PlayerActionC2SPacket.Action.START_DESTROY_BLOCK;
        networkHandler.sendPacket(new PlayerActionC2SPacket(action, blockPos, bhr.getSide()));
    }

    private boolean checkBlockIsHarvestable(MinecraftClient client, BlockPos blockPos) {
        var state = client.world.getBlockState(blockPos);
        var block = state.getBlock();
        var maxAge = 0;
        var age = 0;
        if (block instanceof NetherWartBlock) {
            maxAge = 3;
            age = state.get(NetherWartBlock.AGE);
        } else if (block instanceof CropBlock cropBlock) {
            maxAge = cropBlock.getMaxAge();
            age = state.get(cropBlock.getAgeProperty());
        } else
            return false;
        return age == maxAge;
    }

}
