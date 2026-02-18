package chneau.autotool;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
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
		if (client.player == null || client.level == null || client.options.hideGui)
			return;
		Config config = ConfigManager.getConfig();
		List<Scanner.Target> allPotentialTargets = new ArrayList<>();
		// 1. Entities (Optimized: only scan every 10 frames)
		if (Throttler.shouldRun(this, 10)) {
			categoryEntityTargets = Scanner.scanEntities(client, config);
		}
		addLimitedTargets(allPotentialTargets, categoryEntityTargets.get("Monster"), config.targetMonster,
				client.player.position());
		addLimitedTargets(allPotentialTargets, categoryEntityTargets.get("Player"), config.targetPlayer,
				client.player.position());
		addLimitedTargets(allPotentialTargets, categoryEntityTargets.get("Passive"), config.targetPassive,
				client.player.position());
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
		addLimitedTargets(allPotentialTargets, currentBlockResults.get("Diamond"), config.targetDiamond,
				client.player.position());
		addLimitedTargets(allPotentialTargets, currentBlockResults.get("Emerald"), config.targetEmerald,
				client.player.position());
		addLimitedTargets(allPotentialTargets, currentBlockResults.get("Gold"), config.targetGold,
				client.player.position());
		addLimitedTargets(allPotentialTargets, currentBlockResults.get("Iron"), config.targetIron,
				client.player.position());
		addLimitedTargets(allPotentialTargets, currentBlockResults.get("Debris"), config.targetDebris,
				client.player.position());
		addLimitedTargets(allPotentialTargets, currentBlockResults.get("Chest"), config.targetChest,
				client.player.position());
		addLimitedTargets(allPotentialTargets, currentBlockResults.get("Spawner"), config.targetSpawner,
				client.player.position());
		// Improved sorting: priority first, then distance
		allPotentialTargets.sort(Comparator.<Scanner.Target>comparingInt(t -> t.priority)
				.thenComparingDouble(t -> client.player.distanceToSqr(t.pos)));
		List<Scanner.Target> finalTargets = allPotentialTargets.stream().limit(config.targetLimit)
				.collect(Collectors.toList());
		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();
		// Calculate text widths
		List<TargetInfo> renderInfos = new ArrayList<>();
		int maxTextWidth = 0;
		for (Scanner.Target target : finalTargets) {
			double diffX = target.pos.x - client.player.getX();
			double diffY = target.pos.y - client.player.getEyeY();
			double diffZ = target.pos.z - client.player.getZ();
			float angleToTarget = (float) Math.toDegrees(Math.atan2(-diffX, diffZ));
			float relativeYaw = Mth
					.wrapDegrees(angleToTarget - client.player.getYRot(tickCounter.getGameTimeDeltaTicks()));
			double horizDist = Math.sqrt(diffX * diffX + diffZ * diffZ);
			// Minecraft Pitch: -90 is UP, +90 is DOWN.
			// atan2(y, dist): Positive y (up) gives positive angle.
			// We need to negate it to match MC's pitch.
			float anglePitch = (float) -Math.toDegrees(Math.atan2(diffY, horizDist));
			float relativePitch = anglePitch - client.player.getXRot(tickCounter.getGameTimeDeltaTicks());
			double dist = Math.sqrt(client.player.distanceToSqr(target.pos));
			String emoji = getCategoryEmoji(target.category);
			String text = String.format("%s   %.1fm %s", emoji, dist, target.name);
			renderInfos.add(new TargetInfo(text, relativeYaw, relativePitch, config.targetHudColor));
			maxTextWidth = Math.max(maxTextWidth, client.font.width(text));
		}
		if (renderInfos.isEmpty())
			return;
		int totalHeight = renderInfos.size() * 12;
		int startY;
		if (config.targetHudPosition == Config.HudPosition.BOTTOM_LEFT
				|| config.targetHudPosition == Config.HudPosition.BOTTOM_RIGHT) {
			startY = screenHeight - 10 - totalHeight;
		} else {
			startY = 10;
		}
		int xBase;
		boolean isRightSide = config.targetHudPosition == Config.HudPosition.TOP_RIGHT
				|| config.targetHudPosition == Config.HudPosition.BOTTOM_RIGHT;
		if (isRightSide) {
			xBase = screenWidth - 10 - maxTextWidth;
		} else {
			xBase = 10;
		}
		int y = startY;
		for (TargetInfo info : renderInfos) {
			int x = xBase;
			if (isRightSide) {
				x = screenWidth - 10 - client.font.width(info.text);
			}
			// Draw text
			drawContext.drawString(client.font, info.text, x, y, info.color, true);
			// Draw arrow (inserted in the gap)
			// The gap is after the emoji (approx 2 chars width)
			int emojiWidth = client.font.width("XX "); // Approximate width for emoji + space
			int arrowX = x + emojiWidth - 4; // Adjust position to fit in the gap
			int arrowY = y + 4; // Center vertically (font height is 9, line height 12)
			Draw.drawArrow(drawContext, arrowX, arrowY, info.relativeYaw, info.relativePitch, info.color);
			y += 12;
		}
	}
	private record TargetInfo(String text, float relativeYaw, float relativePitch, int color) {
	}
	private String getCategoryEmoji(String category) {
		if (category == null)
			return "";
		return switch (category) {
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
	private void addLimitedTargets(List<Scanner.Target> allPotentialTargets, List<Scanner.Target> categoryList,
			int limit, Vec3 playerPos) {
		if (categoryList == null || limit <= 0)
			return;
		List<Scanner.Target> sorted = new ArrayList<>(categoryList); // Defensive copy
		sorted.sort(Comparator.comparingDouble(t -> t.pos.distanceToSqr(playerPos)));
		allPotentialTargets.addAll(sorted.stream().limit(limit).collect(Collectors.toList()));
	}
}
