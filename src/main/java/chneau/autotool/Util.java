package chneau.autotool;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Util {
    private final static double wee = 1e-6;

    public static BlockPos getTargetedBlock(MinecraftClient c) {
        Vec3d cameraPos = c.cameraEntity.getCameraPosVec(1);
        Vec3d pos = c.crosshairTarget.getPos();
        double x = (pos.x - cameraPos.x > 0) ? wee : -wee;
        double y = (pos.y - cameraPos.y > 0) ? wee : -wee;
        double z = (pos.z - cameraPos.z > 0) ? wee : -wee;
        pos = pos.add(x, y, z);
        BlockPos blockPos = new BlockPos(pos);
        return blockPos;
    }
}
