package chneau.autotool;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

public class Util {
    private Util() {
    }

    private static final double WEE = 1e-6;

    public static BlockPos getTargetedBlock(Minecraft client) {
        if (client.getCameraEntity() == null || client.hitResult == null) {
            return null;
        }
        var cameraPos = client.getCameraEntity().getEyePosition(1.0f);
        var pos = client.hitResult.getLocation();
        var x = (pos.x - cameraPos.x > 0) ? WEE : -WEE;
        var y = (pos.y - cameraPos.y > 0) ? WEE : -WEE;
        var z = (pos.z - cameraPos.z > 0) ? WEE : -WEE;
        pos = pos.add(x, y, z);
        return new BlockPos.MutableBlockPos(pos.x, pos.y, pos.z);
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
