package chneau.autotool;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class Util {
    private final static double wee = 1e-6;

    public static BlockPos getTargetedBlock(MinecraftClient client) {
        var cameraPos = client.cameraEntity.getCameraPosVec(1);
        var pos = client.crosshairTarget.getPos();
        var x = (pos.x - cameraPos.x > 0) ? wee : -wee;
        var y = (pos.y - cameraPos.y > 0) ? wee : -wee;
        var z = (pos.z - cameraPos.z > 0) ? wee : -wee;
        pos = pos.add(x, y, z);
        var blockPos = new BlockPos(pos);
        return blockPos;
    }

    public static boolean isCurrentPlayer(PlayerEntity other) {
        var instance = MinecraftClient.getInstance();
        var player = instance.player;
        if (player == null)
            return false;
        if (other == null)
            return false;
        return player.equals(other);
    }
}
