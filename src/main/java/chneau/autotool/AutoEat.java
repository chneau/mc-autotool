package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
public class AutoEat extends BaseModule implements ClientTickEvents.EndTick {
	private long last = 0;
	private int lastS = -1;
	private boolean eating = false;
	private double lx, ly, lz;
	@Override
	public void onEndTick(Minecraft c) {
		var m = config().autoEat;
		if (m == Config.EatMode.OFF) {
			if (eating)
				stop(c);
			return;
		}
		var p = c.player;
		boolean act = p.getX() != lx || p.getY() != ly || p.getZ() != lz || c.options.keyAttack.isDown()
				|| (c.options.keyUse.isDown() && !eating) || c.options.keyJump.isDown() || c.options.keyShift.isDown()
				|| c.options.keySprint.isDown() || c.screen != null;
		lx = p.getX();
		ly = p.getY();
		lz = p.getZ();
		if (act) {
			last = System.currentTimeMillis();
			if (eating)
				stop(c);
			return;
		}
		if (System.currentTimeMillis() - last < 1000)
			return;
		if (eating) {
			if (p.isUsingItem())
				return;
			if (!should(p, m)) {
				stop(c);
				return;
			}
			eating = false;
		}
		if (should(p, m)) {
			int s = find(p.getInventory(), p.getFoodData().getFoodLevel(), m == Config.EatMode.SMART);
			if (s != -1)
				start(c, s);
			else if (eating)
				stop(c);
		}
	}
	private boolean should(net.minecraft.world.entity.player.Player p, Config.EatMode m) {
		int h = p.getFoodData().getFoodLevel();
		float hl = p.getHealth(), mh = p.getMaxHealth();
		return (m == Config.EatMode.HUNGER && h < 20) || (m == Config.EatMode.HEALTH && hl < mh)
				|| (m == Config.EatMode.SMART && (h <= 14 || (hl < mh && h < 20)));
	}
	private int find(net.minecraft.world.entity.player.Inventory inv, int h, boolean s) {
		int best = -1, need = 20 - h, bestDiff = Integer.MAX_VALUE;
		for (int i = 0; i < 9; i++) {
			var f = inv.getItem(i).get(DataComponents.FOOD);
			if (f != null) {
				int v = f.nutrition();
				if (s) {
					if (h > 10 && v > need + 4)
						continue;
					if (Math.abs(v - need) < bestDiff) {
						bestDiff = Math.abs(v - need);
						best = i;
					}
				} else if (best == -1 || v > inv.getItem(best).get(DataComponents.FOOD).nutrition())
					best = i;
			}
		}
		return best;
	}
	private void start(Minecraft c, int s) {
		if (lastS == -1)
			lastS = c.player.getInventory().getSelectedSlot();
		Util.selectSlot(c, s);
		c.options.keyUse.setDown(true);
		eating = true;
	}
	private void stop(Minecraft c) {
		c.options.keyUse.setDown(false);
		if (lastS != -1 && c.player != null)
			Util.selectSlot(c, lastS);
		lastS = -1;
		eating = false;
	}
}
