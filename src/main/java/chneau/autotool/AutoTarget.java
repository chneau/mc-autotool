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
		if (client.player == null || client.level == null)
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

			drawArrow(drawContext, arrowX, arrowY, info.relativeYaw, info.relativePitch, info.color);

			y += 12;
		}
	}

	private record TargetInfo(String text, float relativeYaw, float relativePitch, int color) {
	}

	private void drawArrow(GuiGraphics gfx, int x, int y, float yaw, float pitch, int baseColor) {
		// Define 3D Arrow Vertices for a "Round" look (Approximated with 8-sided
		// cylinders/cones)
		float s = 8.0f;
		List<Polygon> polygons = new ArrayList<>();

		// 1. Shaft (Cylinder) - 8 sides
		// Z from -0.5 to 0.2, Radius 0.05
		createCylinder(polygons, -0.5f, 0.2f, 0.05f, 8, baseColor);

		// 2. Head (Cone) - 8 sides
		// Z from 0.2 to 0.6, Radius base 0.15, Radius tip 0
		createCone(polygons, 0.2f, 0.6f, 0.15f, 8, 0xFFFF0000); // Red tip for contrast

		// Rotation logic
		float yawRad = (float) Math.toRadians(yaw);
		float pitchRad = (float) Math.toRadians(pitch);

		// Transform and Project Polygons
		List<ProjectedPolygon> projectedPolys = new ArrayList<>();
		Vec3 lightDir = new Vec3(0.2, -0.5, -1.0).normalize(); // Light from top-left-front

		for (Polygon poly : polygons) {
			Vec3[] transformedVerts = new Vec3[poly.vertices.length];
			Vec3 center = Vec3.ZERO;

			for (int i = 0; i < poly.vertices.length; i++) {
				Vec3 v = poly.vertices[i];
				// Rotate Pitch (X)
				double y1 = v.y * Math.cos(pitchRad) - v.z * Math.sin(pitchRad);
				double z1 = v.y * Math.sin(pitchRad) + v.z * Math.cos(pitchRad);
				double x1 = v.x;
				// Rotate Yaw (Y)
				double x2 = x1 * Math.cos(yawRad) + z1 * Math.sin(yawRad);
				double z2 = -x1 * Math.sin(yawRad) + z1 * Math.cos(yawRad);
				double y2 = y1;

				transformedVerts[i] = new Vec3(x2, y2, z2);
				center = center.add(transformedVerts[i]);
			}
			center = center.scale(1.0 / poly.vertices.length);

			// Back-face culling & Shading
			// Calculate Normal
			Vec3 v0 = transformedVerts[0];
			Vec3 v1 = transformedVerts[1];
			Vec3 v2 = transformedVerts[2];
			Vec3 edge1 = v1.subtract(v0);
			Vec3 edge2 = v2.subtract(v1);
			Vec3 normal = edge1.cross(edge2).normalize();

			// If normal.z > 0, the face is facing away (if using standard GL coords).
			// However, since we are implementing manual software rendering, let's just use
			// Z-sorting painters algorithm.
			// But for shading we need the normal.

			// Simple shading: dot product with light
			double brightness = Math.max(0.3, Math.abs(normal.dot(lightDir)));
			int shadedColor = applyShading(poly.color != 0 ? poly.color : baseColor, (float) brightness);

			// Project to 2D
			Vec3[] screenVerts = new Vec3[transformedVerts.length];
			for (int i = 0; i < transformedVerts.length; i++) {
				Vec3 v = transformedVerts[i];
				screenVerts[i] = new Vec3(x + v.x * s, y - v.y * s, v.z); // Invert Y for screen
			}

			projectedPolys.add(new ProjectedPolygon(screenVerts, shadedColor, center.z));
		}

		// Sort by Z (depth) - Draw furthest first
		projectedPolys.sort((a, b) -> Double.compare(b.z, a.z)); // Draw back to front

		// Draw Filled Polygons
		for (ProjectedPolygon p : projectedPolys) {
			drawPolygon(gfx, p.verts, p.color);
		}
	}

	private int applyShading(int color, float brightness) {
		int a = (color >> 24) & 0xFF;
		int r = (int) (((color >> 16) & 0xFF) * brightness);
		int g = (int) (((color >> 8) & 0xFF) * brightness);
		int b = (int) ((color & 0xFF) * brightness);
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private void drawPolygon(GuiGraphics gfx, Vec3[] verts, int color) {
		if (verts.length == 3) {
			drawFilledTriangle(gfx, (int) verts[0].x, (int) verts[0].y, (int) verts[1].x, (int) verts[1].y,
					(int) verts[2].x, (int) verts[2].y, color);
		} else if (verts.length == 4) {
			// Split quad into 2 triangles
			drawFilledTriangle(gfx, (int) verts[0].x, (int) verts[0].y, (int) verts[1].x, (int) verts[1].y,
					(int) verts[2].x, (int) verts[2].y, color);
			drawFilledTriangle(gfx, (int) verts[2].x, (int) verts[2].y, (int) verts[3].x, (int) verts[3].y,
					(int) verts[0].x, (int) verts[0].y, color);
		}
	}

	private void drawFilledTriangle(GuiGraphics gfx, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
		// Sort vertices by y-coordinate ascending
		if (y1 > y2) {
			int t = y1;
			y1 = y2;
			y2 = t;
			t = x1;
			x1 = x2;
			x2 = t;
		}
		if (y1 > y3) {
			int t = y1;
			y1 = y3;
			y3 = t;
			t = x1;
			x1 = x3;
			x3 = t;
		}
		if (y2 > y3) {
			int t = y2;
			y2 = y3;
			y3 = t;
			t = x2;
			x2 = x3;
			x3 = t;
		}

		if (y1 == y3)
			return; // Zero height

		// Scanline rasterization
		for (int y = y1; y <= y3; y++) {
			double progressLong = (double) (y - y1) / (y3 - y1);
			int xa = (int) (x1 + (x3 - x1) * progressLong);

			int xb;
			if (y < y2) {
				double progressShort = (double) (y - y1) / (y2 - y1);
				xb = (int) (x1 + (x2 - x1) * progressShort);
			} else {
				if (y3 == y2)
					xb = x2;
				else {
					double progressShort = (double) (y - y2) / (y3 - y2);
					xb = (int) (x2 + (x3 - x2) * progressShort);
				}
			}

			int minX = Math.min(xa, xb);
			int maxX = Math.max(xa, xb);

			gfx.fill(minX, y, maxX + 1, y + 1, color);
		}
	}

	private void createCylinder(List<Polygon> polys, float zStart, float zEnd, float radius, int sides, int color) {
		float angleStep = (float) (2 * Math.PI / sides);
		// Side faces
		for (int i = 0; i < sides; i++) {
			float theta1 = i * angleStep;
			float theta2 = (i + 1) * angleStep;

			Vec3 p1 = new Vec3(radius * Math.cos(theta1), radius * Math.sin(theta1), zStart);
			Vec3 p2 = new Vec3(radius * Math.cos(theta2), radius * Math.sin(theta2), zStart);
			Vec3 p3 = new Vec3(radius * Math.cos(theta2), radius * Math.sin(theta2), zEnd);
			Vec3 p4 = new Vec3(radius * Math.cos(theta1), radius * Math.sin(theta1), zEnd);

			polys.add(new Polygon(new Vec3[]{p1, p2, p3, p4}, color));
		}
		// End caps (simplified)
		Vec3[] capStart = new Vec3[sides];
		Vec3[] capEnd = new Vec3[sides];
		for (int i = 0; i < sides; i++) {
			float theta = i * angleStep;
			capStart[sides - 1 - i] = new Vec3(radius * Math.cos(theta), radius * Math.sin(theta), zStart);
			capEnd[i] = new Vec3(radius * Math.cos(theta), radius * Math.sin(theta), zEnd);
		}
		polys.add(new Polygon(capStart, color));
		polys.add(new Polygon(capEnd, color));
	}

	private void createCone(List<Polygon> polys, float zBase, float zTip, float radius, int sides, int color) {
		float angleStep = (float) (2 * Math.PI / sides);
		// Side faces
		for (int i = 0; i < sides; i++) {
			float theta1 = i * angleStep;
			float theta2 = (i + 1) * angleStep;

			Vec3 p1 = new Vec3(radius * Math.cos(theta1), radius * Math.sin(theta1), zBase);
			Vec3 p2 = new Vec3(radius * Math.cos(theta2), radius * Math.sin(theta2), zBase);
			Vec3 tip = new Vec3(0, 0, zTip);

			polys.add(new Polygon(new Vec3[]{p1, p2, tip}, color));
		}
		// Base cap
		Vec3[] capBase = new Vec3[sides];
		for (int i = 0; i < sides; i++) {
			float theta = i * angleStep;
			capBase[sides - 1 - i] = new Vec3(radius * Math.cos(theta), radius * Math.sin(theta), zBase);
		}
		polys.add(new Polygon(capBase, color));
	}

	private record Polygon(Vec3[] vertices, int color) {
	}
	private record ProjectedPolygon(Vec3[] verts, int color, double z) {
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
