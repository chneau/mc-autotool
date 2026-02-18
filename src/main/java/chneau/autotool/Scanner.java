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
import java.util.*;
import java.util.function.Predicate;
public class Scanner {
	public static final int BLOCK_SCAN_RADIUS = 16;
	public record Target(String category, String name, Vec3 pos, int priority) {
	}
	public static Map<String, List<Target>> scanEntities(Minecraft client, Config config) {
		Map<String, List<Target>> res = new HashMap<>();
		if (client.level == null || client.player == null)
			return res;
		for (var e : client.level.entitiesForRendering()) {
			if (!(e instanceof LivingEntity l) || !l.isAlive() || l == client.player)
				continue;
			String cat = (config.targetMonster > 0 && l instanceof Monster)
					? "Monster"
					: (config.targetPlayer > 0 && l instanceof Player)
							? "Player"
							: (config.targetPassive > 0) ? "Passive" : null;
			if (cat != null)
				res.computeIfAbsent(cat, k -> new ArrayList<>())
						.add(new Target(cat, l.getName().getString(), l.position(), cat.equals("Monster") ? 10 : 5));
		}
		return res;
	}
	private record Rule(Predicate<BlockState> check, String cat, String name, int prio) {
	}
	public static Map<String, List<Target>> scanBlocks(Minecraft client, Config config) {
		Map<String, List<Target>> res = new HashMap<>();
		if (client.level == null || client.player == null)
			return res;
		List<Rule> rules = new ArrayList<>();
		if (config.targetDiamond > 0)
			rules.add(new Rule(s -> s.is(Blocks.DIAMOND_ORE) || s.is(Blocks.DEEPSLATE_DIAMOND_ORE), "Diamond",
					"Diamond Ore", 20));
		if (config.targetEmerald > 0)
			rules.add(new Rule(s -> s.is(Blocks.EMERALD_ORE) || s.is(Blocks.DEEPSLATE_EMERALD_ORE), "Emerald",
					"Emerald Ore", 25));
		if (config.targetGold > 0)
			rules.add(new Rule(
					s -> s.is(Blocks.GOLD_ORE) || s.is(Blocks.DEEPSLATE_GOLD_ORE) || s.is(Blocks.NETHER_GOLD_ORE),
					"Gold", "Gold Ore", 35));
		if (config.targetIron > 0)
			rules.add(new Rule(s -> s.is(Blocks.IRON_ORE) || s.is(Blocks.DEEPSLATE_IRON_ORE), "Iron", "Iron Ore", 40));
		if (config.targetDebris > 0)
			rules.add(new Rule(s -> s.is(Blocks.ANCIENT_DEBRIS), "Debris", "Ancient Debris", 15));
		if (config.targetChest > 0)
			rules.add(new Rule(s -> s.is(Blocks.CHEST) || s.is(Blocks.TRAPPED_CHEST) || s.is(Blocks.BARREL)
					|| s.is(BlockTags.SHULKER_BOXES), "Chest", null, 30));
		if (config.targetSpawner > 0)
			rules.add(new Rule(s -> s.is(Blocks.SPAWNER), "Spawner", "Mob Spawner", 22));
		BlockPos p = client.player.blockPosition();
		for (int x = -16; x <= 16; x++)
			for (int y = -16; y <= 16; y++)
				for (int z = -16; z <= 16; z++) {
					var pos = p.offset(x, y, z);
					var state = client.level.getBlockState(pos);
					for (var r : rules)
						if (r.check.test(state)) {
							res.computeIfAbsent(r.cat, k -> new ArrayList<>())
									.add(new Target(r.cat,
											r.name == null ? state.getBlock().getName().getString() : r.name,
											Vec3.atCenterOf(pos), r.prio));
							break;
						}
				}
		return res;
	}
}
