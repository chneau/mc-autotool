package chneau.autotool;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Draw {
	public record Polygon(Vec3[] vertices, int color) {
	}

	private record ProjectedPolygon(Vec3[] verts, int color, double z) {
	}

	public static final List<Polygon> ARROW_MODEL = createArrowModel();

	private static List<Polygon> createArrowModel() {
		List<Polygon> polys = new ArrayList<>();
		createCylinder(polys, -0.5f, 0.2f, 0.05f, 8, 0xFFFFFFFF);
		createCone(polys, 0.2f, 0.6f, 0.15f, 8, 0xFFFF0000);
		return polys;
	}

	public static void drawArrow(GuiGraphics gfx, int x, int y, float yaw, float pitch, int baseColor) {
		float s = 8.0f;
		float yawRad = (float) Math.toRadians(yaw);
		float pitchRad = (float) Math.toRadians(pitch);
		Vec3 lightDir = new Vec3(0.2, -0.5, -1.0).normalize();

		List<ProjectedPolygon> projected = new ArrayList<>();

		for (Polygon poly : ARROW_MODEL) {
			Vec3[] screenVerts = new Vec3[poly.vertices.length];
			Vec3 center = Vec3.ZERO;

			for (int i = 0; i < poly.vertices.length; i++) {
				Vec3 v = poly.vertices[i];
				// Pitch then Yaw rotation
				double y1 = v.y * Math.cos(pitchRad) - v.z * Math.sin(pitchRad);
				double z1 = v.y * Math.sin(pitchRad) + v.z * Math.cos(pitchRad);
				double x2 = v.x * Math.cos(yawRad) + z1 * Math.sin(yawRad);
				double z2 = -v.x * Math.sin(yawRad) + z1 * Math.cos(yawRad);

				center = center.add(new Vec3(x2, y1, z2));
				screenVerts[i] = new Vec3(x + x2 * s, y - y1 * s, z2);
			}
			center = center.scale(1.0 / poly.vertices.length);

			// Shading
			Vec3 v0 = poly.vertices[0], v1 = poly.vertices[1], v2 = poly.vertices[2];
			Vec3 normal = v1.subtract(v0).cross(v2.subtract(v1)).normalize();
			// Re-rotate normal for correct lighting
			double ny1 = normal.y * Math.cos(pitchRad) - normal.z * Math.sin(pitchRad);
			double nz1 = normal.y * Math.sin(pitchRad) + normal.z * Math.cos(pitchRad);
			double nx2 = normal.x * Math.cos(yawRad) + nz1 * Math.sin(yawRad);
			double nz2 = -normal.x * Math.sin(yawRad) + nz1 * Math.cos(yawRad);
			Vec3 rotNormal = new Vec3(nx2, ny1, nz2);

			double brightness = Math.max(0.3, Math.abs(rotNormal.dot(lightDir)));
			int color = applyShading(poly.color == 0xFFFFFFFF ? baseColor : poly.color, (float) brightness);

			projected.add(new ProjectedPolygon(screenVerts, color, center.z));
		}

		projected.sort((a, b) -> Double.compare(b.z, a.z));
		for (ProjectedPolygon p : projected)
			drawPolygon(gfx, p.verts, p.color);
	}

	private static int applyShading(int c, float b) {
		return (c & 0xFF000000) | ((int) ((c >> 16 & 0xFF) * b) << 16) | ((int) ((c >> 8 & 0xFF) * b) << 8)
				| (int) ((c & 0xFF) * b);
	}

	private static void drawPolygon(GuiGraphics gfx, Vec3[] v, int color) {
		if (v.length == 3)
			drawTri(gfx, v[0], v[1], v[2], color);
		else if (v.length == 4) {
			drawTri(gfx, v[0], v[1], v[2], color);
			drawTri(gfx, v[2], v[3], v[0], color);
		}
	}

	private static void drawTri(GuiGraphics gfx, Vec3 v1, Vec3 v2, Vec3 v3, int color) {
		int x1 = (int) v1.x, y1 = (int) v1.y, x2 = (int) v2.x, y2 = (int) v2.y, x3 = (int) v3.x, y3 = (int) v3.y;
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
			return;

		for (int y = y1; y <= y3; y++) {
			int xa = (y3 == y1) ? x1 : x1 + (x3 - x1) * (y - y1) / (y3 - y1);
			int xb;
			if (y < y2) {
				xb = (y2 == y1) ? x1 : x1 + (x2 - x1) * (y - y1) / (y2 - y1);
			} else {
				xb = (y3 == y2) ? x2 : x2 + (x3 - x2) * (y - y2) / (y3 - y2);
			}
			gfx.fill(Math.min(xa, xb), y, Math.max(xa, xb) + 1, y + 1, color);
		}
	}

	private static void createCylinder(List<Polygon> polys, float z1, float z2, float r, int sides, int color) {
		float step = (float) (2 * Math.PI / sides);
		for (int i = 0; i < sides; i++) {
			float t1 = i * step, t2 = (i + 1) * step;
			Vec3 p1 = new Vec3(r * Math.cos(t1), r * Math.sin(t1), z1);
			Vec3 p2 = new Vec3(r * Math.cos(t2), r * Math.sin(t2), z1);
			polys.add(new Polygon(new Vec3[]{p1, p2, new Vec3(p2.x, p2.y, z2), new Vec3(p1.x, p1.y, z2)}, color));
			polys.add(new Polygon(new Vec3[]{p1, p2, new Vec3(0, 0, z1)}, color)); // Cap
			polys.add(new Polygon(new Vec3[]{new Vec3(p1.x, p1.y, z2), new Vec3(p2.x, p2.y, z2), new Vec3(0, 0, z2)},
					color)); // Cap
		}
	}

	private static void createCone(List<Polygon> polys, float z1, float z2, float r, int sides, int color) {
		float step = (float) (2 * Math.PI / sides);
		for (int i = 0; i < sides; i++) {
			float t1 = i * step, t2 = (i + 1) * step;
			Vec3 p1 = new Vec3(r * Math.cos(t1), r * Math.sin(t1), z1);
			Vec3 p2 = new Vec3(r * Math.cos(t2), r * Math.sin(t2), z1);
			polys.add(new Polygon(new Vec3[]{p1, p2, new Vec3(0, 0, z2)}, color)); // Side
			polys.add(new Polygon(new Vec3[]{p1, p2, new Vec3(0, 0, z1)}, color)); // Base
		}
	}
}
