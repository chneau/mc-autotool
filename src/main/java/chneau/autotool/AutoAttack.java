package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.EntityHitResult;
public class AutoAttack extends BaseModule implements ClientTickEvents.EndTick {
	private long last = 0, delay = 0;
	private ItemStack lastStack = ItemStack.EMPTY;
	@Override
	public void onEndTick(Minecraft c) {
		var m = config().autoAttack;
		if (m == Config.AttackMode.OFF)
			return;
		var p = c.player;
		if (p.getInventory() == null || c.level == null)
			return;
		var t = (c.hitResult instanceof EntityHitResult ehr) ? ehr.getEntity() : null;
		if (t == null)
			for (var e : c.level.entitiesForRendering())
				if (e instanceof Monster mo && mo.isAlive() && mo.distanceTo(p) <= 3.5) {
					t = mo;
					break;
				}
		if (!(t instanceof LivingEntity le) || !le.isAlive())
			return;
		var isMonster = t instanceof Monster;
		var isFocused = c.hitResult instanceof EntityHitResult ehr && ehr.getEntity() == t;
		var h = p.getMainHandItem();
		if (m == Config.AttackMode.SWORD) {
			if (!h.is(ItemTags.SWORDS))
				return;
			if (!isMonster && !isFocused)
				return;
		} else if (m == Config.AttackMode.OMNI) {
			if (isMonster) {
				var s = Select.find(p.getInventory(), Select.HOTBAR_SIZE,
						stack -> stack.is(ItemTags.SWORDS)
								? Util.getWeaponDamage(stack) * Util.getWeaponSpeed(stack)
								: 0,
						0.0);
				if (s != -1 && s != p.getInventory().getSelectedSlot()) {
					Util.selectSlot(c, s);
					h = p.getInventory().getItem(s);
				}
			} else {
				if (!h.is(ItemTags.SWORDS) || !isFocused)
					return;
			}
		}
		if (isMonster || isFocused) {
			if (!Util.areItemsEqual(h, lastStack)) {
				lastStack = h.copy();
				delay = (long) (1000.0 / Util.getWeaponSpeed(h));
			}
			var now = System.currentTimeMillis();
			if (now - last >= delay && c.gameMode != null) {
				c.gameMode.attack(p, t);
				p.resetAttackStrengthTicker();
				p.swing(InteractionHand.MAIN_HAND);
				last = now;
			}
		}
	}
}
