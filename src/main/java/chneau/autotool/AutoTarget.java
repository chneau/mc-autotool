package chneau.autotool;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AutoTarget {
    private long lastBlockScan = 0;
    private volatile Map<String, List<Scanner.Target>> categoryBlockTargets = new HashMap<>();
    private Map<String, List<Scanner.Target>> categoryEntityTargets = new HashMap<>();
    private boolean isScanning = false;

    public void register() {
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    private void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;

        Config config = ConfigManager.getConfig();
        List<Scanner.Target> allPotentialTargets = new ArrayList<>();

        // 1. Entities (Optimized: only scan every 10 frames)
        if (Throttler.shouldRun(this, 10)) {
            categoryEntityTargets = Scanner.scanEntities(client, config);
        }

        addLimitedTargets(allPotentialTargets, categoryEntityTargets.get("Monster"), config.targetMonster, client.player.position());
        addLimitedTargets(allPotentialTargets, categoryEntityTargets.get("Player"), config.targetPlayer, client.player.position());
        addLimitedTargets(allPotentialTargets, categoryEntityTargets.get("Passive"), config.targetPassive, client.player.position());

        // 2. Blocks (Offloaded to background)
        long now = System.currentTimeMillis();
        if (now - lastBlockScan > 2000 && !isScanning) {
            lastBlockScan = now;
            isScanning = true;
            
            CompletableFuture.runAsync(() -> {
                categoryBlockTargets = Scanner.scanBlocks(client, config); // Atomic swap
                isScanning = false;
            });
        }
        
        Map<String, List<Scanner.Target>> currentBlockResults = categoryBlockTargets; // Capture reference
        addLimitedTargets(allPotentialTargets, currentBlockResults.get("Diamond"), config.targetDiamond, client.player.position());
        addLimitedTargets(allPotentialTargets, currentBlockResults.get("Emerald"), config.targetEmerald, client.player.position());
        addLimitedTargets(allPotentialTargets, currentBlockResults.get("Gold"), config.targetGold, client.player.position());
        addLimitedTargets(allPotentialTargets, currentBlockResults.get("Iron"), config.targetIron, client.player.position());
        addLimitedTargets(allPotentialTargets, currentBlockResults.get("Debris"), config.targetDebris, client.player.position());
        addLimitedTargets(allPotentialTargets, currentBlockResults.get("Chest"), config.targetChest, client.player.position());
        addLimitedTargets(allPotentialTargets, currentBlockResults.get("Spawner"), config.targetSpawner, client.player.position());

        // Improved sorting: priority first, then distance
        allPotentialTargets.sort(Comparator.<Scanner.Target>comparingInt(t -> t.priority)
                                  .thenComparingDouble(t -> client.player.distanceToSqr(t.pos)));
        
        List<Scanner.Target> finalTargets = allPotentialTargets.stream().limit(config.targetLimit).collect(Collectors.toList());

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        
        // Calculate max text width for background
        int maxTextWidth = 0;
        List<String> texts = new ArrayList<>();
        for (Scanner.Target target : finalTargets) {
            String text = getTargetText(client, target, tickCounter);
            texts.add(text);
            maxTextWidth = Math.max(maxTextWidth, client.font.width(text));
        }

        if (texts.isEmpty()) return;

        int totalHeight = texts.size() * 12;
        int startY;
        if (config.targetHudPosition == Config.HudPosition.BOTTOM_LEFT || config.targetHudPosition == Config.HudPosition.BOTTOM_RIGHT) {
            startY = screenHeight - 10 - totalHeight;
        } else {
            startY = 10;
        }

        int xBase;
        if (config.targetHudPosition == Config.HudPosition.TOP_RIGHT || config.targetHudPosition == Config.HudPosition.BOTTOM_RIGHT) {
            xBase = screenWidth - 10 - maxTextWidth;
        } else {
            xBase = 10;
        }

        int y = startY;
        for (String text : texts) {
            int x = xBase;
            if (config.targetHudPosition == Config.HudPosition.TOP_RIGHT || config.targetHudPosition == Config.HudPosition.BOTTOM_RIGHT) {
                x = screenWidth - 10 - client.font.width(text);
            }
            drawContext.drawString(client.font, text, x, y, config.targetHudColor, true);
            y += 12;
        }
    }

    private String getTargetText(Minecraft client, Scanner.Target target, DeltaTracker tickCounter) {
        double diffX = target.pos.x - client.player.getX();
        double diffZ = target.pos.z - client.player.getZ();
        float angleToTarget = (float) Math.toDegrees(Math.atan2(-diffX, diffZ));
        float relativeYaw = Mth.wrapDegrees(angleToTarget - client.player.getYRot(tickCounter.getGameTimeDeltaTicks()));

        String arrow = getDirectionArrow(relativeYaw);
        double dist = Math.sqrt(client.player.distanceToSqr(target.pos));

        return String.format("%s %.1fm %s", arrow, dist, target.name);
    }

    private void addLimitedTargets(List<Scanner.Target> allPotentialTargets, List<Scanner.Target> categoryList, int limit, Vec3 playerPos) {
        if (categoryList == null || limit <= 0) return;
        List<Scanner.Target> sorted = new ArrayList<>(categoryList); // Defensive copy
        sorted.sort(Comparator.comparingDouble(t -> t.pos.distanceToSqr(playerPos)));
        allPotentialTargets.addAll(sorted.stream().limit(limit).collect(Collectors.toList()));
    }

    private void drawInfo(GuiGraphics drawContext, Minecraft client, Scanner.Target target, DeltaTracker tickCounter, int y, Config.HudPosition pos, int screenWidth) {
        // Obsolete, replaced by inline logic in onHudRender
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

    private static class Target {
        final String name;
        final Vec3 pos;

        Target(String name, Vec3 pos) {
            this.name = name;
            this.pos = pos;
        }
    }
}
