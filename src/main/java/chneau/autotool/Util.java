package chneau.autotool;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class Util {
    private Util() {
    }

    private static final double WEE = 1e-6;

    public static BlockPos getTargetedBlock(MinecraftClient client) {
        var cameraPos = client.cameraEntity.getCameraPosVec(1);
        var pos = client.crosshairTarget.getPos();
        var x = (pos.x - cameraPos.x > 0) ? WEE : -WEE;
        var y = (pos.y - cameraPos.y > 0) ? WEE : -WEE;
        var z = (pos.z - cameraPos.z > 0) ? WEE : -WEE;
        pos = pos.add(x, y, z);
        return new BlockPos.Mutable(pos.x, pos.y, pos.z);
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
