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
import java.util.stream.Collectors;

public class AutoTarget {
    private static final int BLOCK_SCAN_RADIUS = 16;
    private long lastBlockScan = 0;
    private final Map<String, List<Target>> categoryBlockTargets = new HashMap<>();

    public void register() {
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    private void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;

        Config config = ConfigManager.getConfig();
        List<Target> allPotentialTargets = new ArrayList<>();

        // 1. Entities
        Map<String, List<Target>> entityCategories = new HashMap<>();
        for (var entity : client.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive() || living == client.player) {
                continue;
            }

            String category = null;
            if (config.targetMonster > 0 && living instanceof Monster) category = "Monster";
            else if (config.targetPlayer > 0 && living instanceof Player) category = "Player";
            else if (config.targetPassive > 0 && !(living instanceof Monster) && !(living instanceof Player)) category = "Passive";

            if (category != null) {
                entityCategories.computeIfAbsent(category, k -> new ArrayList<>()).add(new Target(living.getName().getString(), living.position()));
            }
        }

        // Limit entities per category
        addLimitedTargets(allPotentialTargets, entityCategories.get("Monster"), config.targetMonster, client.player.position());
        addLimitedTargets(allPotentialTargets, entityCategories.get("Player"), config.targetPlayer, client.player.position());
        addLimitedTargets(allPotentialTargets, entityCategories.get("Passive"), config.targetPassive, client.player.position());

        // 2. Blocks
        long now = System.currentTimeMillis();
        if (now - lastBlockScan > 2000) {
            lastBlockScan = now;
            scanBlocks(client, config);
        }
        
        addLimitedTargets(allPotentialTargets, categoryBlockTargets.get("Diamond"), config.targetDiamond, client.player.position());
        addLimitedTargets(allPotentialTargets, categoryBlockTargets.get("Emerald"), config.targetEmerald, client.player.position());
        addLimitedTargets(allPotentialTargets, categoryBlockTargets.get("Gold"), config.targetGold, client.player.position());
        addLimitedTargets(allPotentialTargets, categoryBlockTargets.get("Iron"), config.targetIron, client.player.position());
        addLimitedTargets(allPotentialTargets, categoryBlockTargets.get("Debris"), config.targetDebris, client.player.position());

        // Final sort and global limit (5)
        allPotentialTargets.sort(Comparator.comparingDouble(t -> client.player.distanceToSqr(t.pos)));
        List<Target> finalTargets = allPotentialTargets.stream().limit(5).collect(Collectors.toList());

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int totalHeight = finalTargets.size() * 12;

        int startY;
        if (config.targetHudPosition == Config.HudPosition.BOTTOM_LEFT || config.targetHudPosition == Config.HudPosition.BOTTOM_RIGHT) {
            startY = screenHeight - 10 - totalHeight;
        } else {
            startY = 10;
        }

        int y = startY;
        for (Target target : finalTargets) {
            drawInfo(drawContext, client, target, tickCounter, y, config.targetHudPosition, screenWidth);
            y += 12;
        }
    }

    private void addLimitedTargets(List<Target> allPotentialTargets, List<Target> categoryList, int limit, Vec3 playerPos) {
        if (categoryList == null || limit <= 0) return;
        categoryList.sort(Comparator.comparingDouble(t -> t.pos.distanceToSqr(playerPos)));
        allPotentialTargets.addAll(categoryList.stream().limit(limit).collect(Collectors.toList()));
    }

    private void scanBlocks(Minecraft client, Config config) {
        categoryBlockTargets.clear();
        BlockPos playerPos = client.player.blockPosition();
        
        for (int x = -BLOCK_SCAN_RADIUS; x <= BLOCK_SCAN_RADIUS; x++) {
            for (int y = -BLOCK_SCAN_RADIUS; y <= BLOCK_SCAN_RADIUS; y++) {
                for (int z = -BLOCK_SCAN_RADIUS; z <= BLOCK_SCAN_RADIUS; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = client.level.getBlockState(pos);
                    
                    String category = null;
                    String name = null;
                    if (config.targetDiamond > 0 && (state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE))) {
                        category = "Diamond"; name = "Diamond Ore";
                    } else if (config.targetEmerald > 0 && (state.is(Blocks.EMERALD_ORE) || state.is(Blocks.DEEPSLATE_EMERALD_ORE))) {
                        category = "Emerald"; name = "Emerald Ore";
                    } else if (config.targetGold > 0 && (state.is(Blocks.GOLD_ORE) || state.is(Blocks.DEEPSLATE_GOLD_ORE) || state.is(Blocks.NETHER_GOLD_ORE))) {
                        category = "Gold"; name = "Gold Ore";
                    } else if (config.targetIron > 0 && (state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE))) {
                        category = "Iron"; name = "Iron Ore";
                    } else if (config.targetDebris > 0 && state.is(Blocks.ANCIENT_DEBRIS)) {
                        category = "Debris"; name = "Ancient Debris";
                    }

                    if (category != null) {
                        categoryBlockTargets.computeIfAbsent(category, k -> new ArrayList<>()).add(new Target(name, Vec3.atCenterOf(pos)));
                    }
                }
            }
        }
    }

    private void drawInfo(GuiGraphics drawContext, Minecraft client, Target target, DeltaTracker tickCounter, int y, Config.HudPosition pos, int screenWidth) {
        double diffX = target.pos.x - client.player.getX();
        double diffZ = target.pos.z - client.player.getZ();
        float angleToTarget = (float) Math.toDegrees(Math.atan2(-diffX, diffZ));
        float relativeYaw = Mth.wrapDegrees(angleToTarget - client.player.getYRot(tickCounter.getGameTimeDeltaTicks()));

        String arrow = getDirectionArrow(relativeYaw);
        double dist = Math.sqrt(client.player.distanceToSqr(target.pos));

        String text = String.format("%s %.1fm %s", arrow, dist, target.name);
        
        int x;
        if (pos == Config.HudPosition.TOP_RIGHT || pos == Config.HudPosition.BOTTOM_RIGHT) {
            x = screenWidth - 10 - client.font.width(text);
        } else {
            x = 10;
        }

        drawContext.drawString(client.font, text, x, y, 0xFFFFFFFF, true);
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
