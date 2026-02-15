package chneau.autotool;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

public class AutoTarget {
    public void register() {
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    private void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter) {
        Config.TargetMode mode = ConfigManager.getConfig().autoTarget;
        if (mode == Config.TargetMode.OFF) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;

        LivingEntity target = null;
        double minDist = Double.MAX_VALUE;

        for (var entity : client.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive() || living == client.player) {
                continue;
            }

            if (mode == Config.TargetMode.MONSTER && !(living instanceof Monster)) {
                continue;
            }

            double dist = client.player.distanceToSqr(living);
            if (dist < minDist) {
                minDist = dist;
                target = living;
            }
        }

        if (target != null) {
            drawInfo(drawContext, client, target, tickCounter);
        }
    }

    private void drawInfo(GuiGraphics drawContext, Minecraft client, LivingEntity target, DeltaTracker tickCounter) {
        double diffX = target.getX() - client.player.getX();
        double diffZ = target.getZ() - client.player.getZ();
        float angleToTarget = (float) Math.toDegrees(Math.atan2(-diffX, diffZ));
        float relativeYaw = Mth.wrapDegrees(angleToTarget - client.player.getYRot(tickCounter.getGameTimeDeltaTicks()));

        String arrow = getDirectionArrow(relativeYaw);
        double dist = client.player.distanceTo(target);
        String name = target.getName().getString();

        String text = String.format("%s %.1fm %s", arrow, dist, name);
        drawContext.drawString(client.font, text, 10, 10, 0xFFFFFFFF, true);
    }

    private String getDirectionArrow(float relativeYaw) {
        if (relativeYaw > -22.5 && relativeYaw <= 22.5) return "↑";
        if (relativeYaw > 22.5 && relativeYaw <= 67.5) return "↗";
        if (relativeYaw > 67.5 && relativeYaw <= 112.5) return "→";
        if (relativeYaw > 112.5 && relativeYaw <= 157.5) return "↘";
        if (relativeYaw > 157.5 || relativeYaw <= -157.5) return "↓";
        if (relativeYaw > -157.5 && relativeYaw <= -112.5) return "↙";
        if (relativeYaw > -112.5 && relativeYaw <= -67.5) return "←";
        if (relativeYaw > -67.5 && relativeYaw <= -22.5) return "↖";
        return "?";
    }
}
