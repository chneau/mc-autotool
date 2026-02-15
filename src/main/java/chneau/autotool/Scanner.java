package chneau.autotool;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.tags.BlockTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    public static final int BLOCK_SCAN_RADIUS = 16;

    public static class Target {
        public final String category;
        public final String name;
        public final Vec3 pos;
        public final int priority;

        public Target(String category, String name, Vec3 pos, int priority) {
            this.category = category;
            this.name = name;
            this.pos = pos;
            this.priority = priority;
        }
    }

    public static Map<String, List<Target>> scanEntities(Minecraft client, Config config) {
        Map<String, List<Target>> categoryEntityTargets = new HashMap<>();
        if (client.level == null || client.player == null) return categoryEntityTargets;

        for (var entity : client.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive() || living == client.player) {
                continue;
            }

            String category = null;
            int priority = 100;
            if (config.targetMonster > 0 && living instanceof Monster) {
                category = "Monster";
                priority = 10;
            }
            else if (config.targetPlayer > 0 && living instanceof Player) {
                category = "Player";
                priority = 5;
            }
            else if (config.targetPassive > 0 && !(living instanceof Monster) && !(living instanceof Player)) {
                category = "Passive";
                priority = 50;
            }

            if (category != null) {
                categoryEntityTargets.computeIfAbsent(category, k -> new ArrayList<>()).add(new Target(category, living.getName().getString(), living.position(), priority));
            }
        }
        return categoryEntityTargets;
    }

    public static Map<String, List<Target>> scanBlocks(Minecraft client, Config config) {
        Map<String, List<Target>> results = new HashMap<>();
        if (client.level == null || client.player == null) return results;

        BlockPos playerPos = client.player.blockPosition();
        var level = client.level;

        for (int x = -BLOCK_SCAN_RADIUS; x <= BLOCK_SCAN_RADIUS; x++) {
            for (int y = -BLOCK_SCAN_RADIUS; y <= BLOCK_SCAN_RADIUS; y++) {
                for (int z = -BLOCK_SCAN_RADIUS; z <= BLOCK_SCAN_RADIUS; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    
                    String category = null;
                    String name = null;
                    int priority = 100;

                    if (config.targetDiamond > 0 && (state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE))) {
                        category = "Diamond"; name = "Diamond Ore"; priority = 20;
                    } else if (config.targetEmerald > 0 && (state.is(Blocks.EMERALD_ORE) || state.is(Blocks.DEEPSLATE_EMERALD_ORE))) {
                        category = "Emerald"; name = "Emerald Ore"; priority = 25;
                    } else if (config.targetGold > 0 && (state.is(Blocks.GOLD_ORE) || state.is(Blocks.DEEPSLATE_GOLD_ORE) || state.is(Blocks.NETHER_GOLD_ORE))) {
                        category = "Gold"; name = "Gold Ore"; priority = 35;
                    } else if (config.targetIron > 0 && (state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE))) {
                        category = "Iron"; name = "Iron Ore"; priority = 40;
                    } else if (config.targetDebris > 0 && state.is(Blocks.ANCIENT_DEBRIS)) {
                        category = "Debris"; name = "Ancient Debris"; priority = 15;
                    } else if (config.targetChest > 0 && (state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST) || state.is(Blocks.BARREL) || state.is(BlockTags.SHULKER_BOXES))) {
                        category = "Chest"; name = state.getBlock().getName().getString(); priority = 30;
                    } else if (config.targetSpawner > 0 && state.is(Blocks.SPAWNER)) {
                        category = "Spawner"; name = "Mob Spawner"; priority = 22;
                    }

                    if (category != null) {
                        results.computeIfAbsent(category, k -> new ArrayList<>()).add(new Target(category, name, Vec3.atCenterOf(pos), priority));
                    }
                }
            }
        }
        return results;
    }
}
