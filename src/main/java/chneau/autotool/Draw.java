package chneau.autotool;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import java.util.*;
public class Draw {
	public record Polygon(Vec3[] vertices, int color) {
	}
	private record ProjPoly(Vec3[] v, int c, double z) {
	}
	public static final List<Polygon> MODEL = create();
	private static List<Polygon> create() {
		List<Polygon> p = new ArrayList<>();
		cyl(p, -0.5f, 0.2f, 0.05f, 8, -1);
		cone(p, 0.2f, 0.6f, 0.15f, 8, 0xFFFF0000);
		return p;
	}
	public static void drawArrow(GuiGraphics g, int x, int y, float yaw, float pitch, int bc) {
		float s = 8f, yr = (float) Math.toRadians(yaw), pr = (float) Math.toRadians(pitch);
		Vec3 ld = new Vec3(0.2, -0.5, -1).normalize();
		List<ProjPoly> proj = new ArrayList<>();
		for (Polygon poly : MODEL) {
			Vec3[] sv = new Vec3[poly.vertices.length];
			Vec3 center = Vec3.ZERO;
			for (int i = 0; i < poly.vertices.length; i++) {
				Vec3 v = poly.vertices[i];
				double y1 = v.y * Math.cos(pr) - v.z * Math.sin(pr), z1 = v.y * Math.sin(pr) + v.z * Math.cos(pr),
						x2 = v.x * Math.cos(yr) + z1 * Math.sin(yr), z2 = -v.x * Math.sin(yr) + z1 * Math.cos(yr);
				center = center.add(new Vec3(x2, y1, z2));
				sv[i] = new Vec3(x + x2 * s, y - y1 * s, z2);
			}
			center = center.scale(1.0 / poly.vertices.length);
			Vec3 v0 = poly.vertices[0], v1 = poly.vertices[1], v2 = poly.vertices[2],
					n = v1.subtract(v0).cross(v2.subtract(v1)).normalize();
			double ny1 = n.y * Math.cos(pr) - n.z * Math.sin(pr), nz1 = n.y * Math.sin(pr) + n.z * Math.cos(pr),
					nx2 = n.x * Math.cos(yr) + nz1 * Math.sin(yr), nz2 = -n.x * Math.sin(yr) + nz1 * Math.cos(yr);
			double b = Math.max(0.3, Math.abs(new Vec3(nx2, ny1, nz2).dot(ld)));
			int c = shade(poly.color == -1 ? bc : poly.color, (float) b);
			proj.add(new ProjPoly(sv, c, center.z));
		}
		proj.sort((a, b) -> Double.compare(b.z, a.z));
		for (ProjPoly p : proj)
			drawPoly(g, p.v, p.c);
	}
	private static int shade(int c, float b) {
		return (c & 0xFF000000) | ((int) ((c >> 16 & 0xFF) * b) << 16) | ((int) ((c >> 8 & 0xFF) * b) << 8)
				| (int) ((c & 0xFF) * b);
	}
	private static void drawPoly(GuiGraphics g, Vec3[] v, int c) {
		tri(g, v[0], v[1], v[2], c);
		if (v.length == 4)
			tri(g, v[2], v[3], v[0], c);
	}
	private static void tri(GuiGraphics g, Vec3 v1, Vec3 v2, Vec3 v3, int c) {
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
			int xa = (y3 == y1) ? x1 : x1 + (x3 - x1) * (y - y1) / (y3 - y1), xb = (y < y2)
					? ((y2 == y1) ? x1 : x1 + (x2 - x1) * (y - y1) / (y2 - y1))
					: ((y3 == y2) ? x2 : x2 + (x3 - x2) * (y - y2) / (y3 - y2));
			g.fill(Math.min(xa, xb), y, Math.max(xa, xb) + 1, y + 1, c);
		}
	}
	private static void cyl(List<Polygon> p, float z1, float z2, float r, int s, int c) {
		float step = (float) (2 * Math.PI / s);
		for (int i = 0; i < s; i++) {
			float t1 = i * step, t2 = (i + 1) * step;
			Vec3 p1 = new Vec3(r * Math.cos(t1), r * Math.sin(t1), z1),
					p2 = new Vec3(r * Math.cos(t2), r * Math.sin(t2), z1);
			p.add(new Polygon(new Vec3[]{p1, p2, new Vec3(p2.x, p2.y, z2), new Vec3(p1.x, p1.y, z2)}, c));
			p.add(new Polygon(new Vec3[]{p1, p2, new Vec3(0, 0, z1)}, c));
			p.add(new Polygon(new Vec3[]{new Vec3(p1.x, p1.y, z2), new Vec3(p2.x, p2.y, z2), new Vec3(0, 0, z2)}, c));
		}
	}
	private static void cone(List<Polygon> p, float z1, float z2, float r, int s, int c) {
		float step = (float) (2 * Math.PI / s);
		for (int i = 0; i < s; i++) {
			float t1 = i * step, t2 = (i + 1) * step;
			Vec3 p1 = new Vec3(r * Math.cos(t1), r * Math.sin(t1), z1),
					p2 = new Vec3(r * Math.cos(t2), r * Math.sin(t2), z1);
			p.add(new Polygon(new Vec3[]{p1, p2, new Vec3(0, 0, z2)}, c));
			p.add(new Polygon(new Vec3[]{p1, p2, new Vec3(0, 0, z1)}, c));
		}
	}
}
