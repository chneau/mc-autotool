package chneau.autotool;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import java.util.*;
public class AutoTarget extends BaseModule implements HudRenderCallback {
	private long lastBS = 0;
	private volatile Map<String, List<Scanner.Target>> bTargets = new HashMap<>();
	private Map<String, List<Scanner.Target>> eTargets = new HashMap<>();
	private boolean scanning = false;
	@Override
	public void onHudRender(GuiGraphics g, DeltaTracker dt) {
		var c = client();
		if (c.player == null || c.level == null || c.options.hideGui)
			return;
		var cfg = config();
		if (Throttler.shouldRun(this, 10))
			eTargets = Scanner.scanEntities(c, cfg);
		if (System.currentTimeMillis() - lastBS > 2000 && !scanning) {
			lastBS = System.currentTimeMillis();
			scanning = true;
			Safe.async(name + ".scan", () -> {
				bTargets = Scanner.scanBlocks(c, cfg);
				scanning = false;
			});
		}
		var all = new ArrayList<Scanner.Target>();
		add(all, eTargets.get("Monster"), cfg.targetMonster);
		add(all, eTargets.get("Player"), cfg.targetPlayer);
		add(all, eTargets.get("Passive"), cfg.targetPassive);
		Map.of("Diamond", cfg.targetDiamond, "Emerald", cfg.targetEmerald, "Gold", cfg.targetGold, "Iron",
				cfg.targetIron, "Debris", cfg.targetDebris, "Chest", cfg.targetChest, "Spawner", cfg.targetSpawner)
				.forEach((k, v) -> add(all, bTargets.get(k), v));
		all.sort(Comparator.<Scanner.Target>comparingInt(t -> t.priority())
				.thenComparingDouble(t -> c.player.distanceToSqr(t.pos())));
		var infos = all.stream().limit(cfg.targetLimit).map(t -> {
			var d = t.pos().subtract(c.player.getX(), c.player.getEyeY(), c.player.getZ());
			return new TInfo(
					String.format("%s   %.1fm %s", getEmoji(t.category()), Math.sqrt(c.player.distanceToSqr(t.pos())),
							t.name()),
					Mth.wrapDegrees((float) Math.toDegrees(Math.atan2(-d.x, d.z))
							- c.player.getYRot(dt.getGameTimeDeltaTicks())),
					(float) -Math.toDegrees(Math.atan2(d.y, Math.sqrt(d.x * d.x + d.z * d.z)))
							- c.player.getXRot(dt.getGameTimeDeltaTicks()));
		}).toList();
		if (infos.isEmpty())
			return;
		int h = infos.size() * 12, sw = c.getWindow().getGuiScaledWidth(), sh = c.getWindow().getGuiScaledHeight(),
				maxW = infos.stream().mapToInt(i -> c.font.width(i.t)).max().orElse(0),
				y = cfg.targetHudPosition.name().startsWith("BOTTOM") ? sh - 10 - h : 10,
				xB = cfg.targetHudPosition.name().endsWith("RIGHT") ? sw - 10 - maxW : 10;
		for (var info : infos) {
			int x = cfg.targetHudPosition.name().endsWith("RIGHT") ? sw - 10 - c.font.width(info.t) : xB;
			g.drawString(c.font, info.t, x, y, cfg.targetHudColor, true);
			Draw.drawArrow(g, x + c.font.width("XX ") - 4, y + 4, info.y, info.p, cfg.targetHudColor);
			y += 12;
		}
	}
	private void add(List<Scanner.Target> all, List<Scanner.Target> l, int lim) {
		if (l != null && lim > 0) {
			var s = new ArrayList<>(l);
			s.sort(Comparator.comparingDouble(t -> t.pos().distanceToSqr(client().player.position())));
			all.addAll(s.stream().limit(lim).toList());
		}
	}
	private String getEmoji(String c) {
		return switch (c) {
			case "Monster" -> "👹";
			case "Player" -> "👤";
			case "Passive" -> "🐷";
			case "Diamond" -> "💎";
			case "Emerald" -> "🟢";
			case "Gold" -> "🟡";
			case "Iron" -> "⚪";
			case "Debris" -> "🧱";
			case "Chest" -> "📦";
			case "Spawner" -> "👾";
			default -> "";
		};
	}
	private record TInfo(String t, float y, float p) {
	}
}
