package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.core.BlockPos;

public class AutoFarm implements EndTick {
    private BlockPos lastTargetedBlock = null;

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        var mode = ConfigManager.getConfig().autoFarm;
        if (mode == Config.FarmMode.OFF)
            return;
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player))
            return;
        if (client.hitResult == null || player.getInventory() == null)
            return;
        
        var targetedBlock = Util.getTargetedBlock(client);
        if (targetedBlock == null) {
            lastTargetedBlock = null;
            return;
        }

        if (targetedBlock.equals(lastTargetedBlock)) {
            return;
        }
        lastTargetedBlock = targetedBlock;

        var inventory = player.getInventory();
        var stackMainHand = inventory.getItem(inventory.getSelectedSlot());
        if (client.hitResult.getType() == Type.BLOCK) {
            var isSeed = stackMainHand.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) || stackMainHand.is(Items.NETHER_WART);
            var isTool = stackMainHand.has(DataComponents.TOOL);
            if (!(isSeed || isTool))
                return;
            var networkHandler = client.getConnection();
            if (networkHandler == null)
                return;
            
            var bhr = (BlockHitResult) client.hitResult;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    var pos = targetedBlock.offset(dx, 0, dz);
                    harvest(client, networkHandler, pos, bhr);
                }
            }

            if (mode == Config.FarmMode.BOTH && isSeed) {
                var basePos = bhr.getBlockPos();
                for (int dy = -1; dy <= 0; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            var pos = basePos.offset(dx, dy, dz);
                            plant(client, networkHandler, pos, bhr);
                        }
                    }
                }
            }
        }
    }

    private void plant(Minecraft client, ClientPacketListener nh, BlockPos blockPos, BlockHitResult bhr) {
        if (client.level == null) return;
        var above = client.level.getBlockState(blockPos.above()).getBlock();
        if (!(above.equals(Blocks.AIR) || checkBlockIsHarvestable(client, blockPos.above())))
            return;
        var block = client.level.getBlockState(blockPos).getBlock();
        if (!(block.equals(Blocks.FARMLAND) || block.equals(Blocks.SOUL_SAND)))
            return;
        bhr = new BlockHitResult(bhr.getLocation(), bhr.getDirection(), blockPos, bhr.isInside());
        nh.send(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, bhr, 0));
    }

    private void harvest(Minecraft client, ClientPacketListener networkHandler, BlockPos blockPos,
            BlockHitResult bhr) {
        if (!checkBlockIsHarvestable(client, blockPos))
            return;
        var action = ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK;
        networkHandler.send(new ServerboundPlayerActionPacket(action, blockPos, bhr.getDirection()));
    }

    private boolean checkBlockIsHarvestable(Minecraft client, BlockPos blockPos) {
        if (client.level == null) return false;
        var state = client.level.getBlockState(blockPos);
        var block = state.getBlock();
        var maxAge = 0;
        var age = 0;
        if (block instanceof NetherWartBlock) {
            maxAge = 3;
            age = state.getValue(NetherWartBlock.AGE);
        } else if (block instanceof CropBlock cropBlock) {
            maxAge = cropBlock.getMaxAge();
            age = cropBlock.getAge(state);
        } else
            return false;
        return age == maxAge;
    }

}
