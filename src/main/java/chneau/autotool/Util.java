package chneau.autotool;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

public class Util {
    private Util() {
    }

    public static BlockPos getTargetedBlock(Minecraft client) {
        if (client.hitResult instanceof BlockHitResult bhr) {
            return bhr.getBlockPos();
        }
        return null;
    }

    public static boolean isCurrentPlayer(Player other) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if (player == null)
            return false;
        if (other == null)
            return false;
        return player.equals(other);
    }
}
