package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
public class AutoEat extends BaseModule implements ClientTickEvents.EndTick {
	private long lastAct = 0;
	private int lastSlot = -1;
	private boolean eating = false;
	private double lx, ly, lz;
	@Override
	public void onEndTick(Minecraft client) {
		var mode = config().autoEat;
		if (mode == Config.EatMode.OFF) {
			if (eating)
				stop(client);
			return;
		}
		var p = client.player;
		boolean act = p.getX() != lx || p.getY() != ly || p.getZ() != lz || client.options.keyAttack.isDown()
				|| (client.options.keyUse.isDown() && !eating) || client.options.keyJump.isDown()
				|| client.options.keyShift.isDown() || client.options.keySprint.isDown() || client.screen != null;
		lx = p.getX();
		ly = p.getY();
		lz = p.getZ();
		if (act) {
			lastAct = System.currentTimeMillis();
			if (eating)
				stop(client);
			return;
		}
		if (System.currentTimeMillis() - lastAct < 1000)
			return;
		if (eating) {
			if (p.isUsingItem())
				return;
			if (!should(p, mode)) {
				stop(client);
				return;
			}
			eating = false;
		}
		if (should(p, mode)) {
			int slot = find(p.getInventory(), p.getFoodData().getFoodLevel(), mode == Config.EatMode.SMART);
			if (slot != -1)
				start(client, slot);
			else if (eating)
				stop(client);
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
		if (lastSlot == -1)
			lastSlot = c.player.getInventory().getSelectedSlot();
		Util.selectSlot(c, s);
		c.options.keyUse.setDown(true);
		eating = true;
	}
	private void stop(Minecraft c) {
		c.options.keyUse.setDown(false);
		if (lastSlot != -1 && c.player != null)
			Util.selectSlot(c, lastSlot);
		lastSlot = -1;
		eating = false;
	}
}
