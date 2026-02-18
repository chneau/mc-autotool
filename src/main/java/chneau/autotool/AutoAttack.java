package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
		var h = p.getMainHandItem();
		if (m == Config.AttackMode.SWORD && !h.is(ItemTags.SWORDS))
			return;
		var t = (c.hitResult instanceof EntityHitResult ehr) ? ehr.getEntity() : null;
		if (t == null)
			for (var e : c.level.entitiesForRendering())
				if (e instanceof Monster mo && mo.isAlive() && mo.distanceTo(p) <= 3.5) {
					t = mo;
					break;
				}
		if (t instanceof LivingEntity le && le.isAlive()
				&& (c.hitResult != null && c.hitResult.getType() == HitResult.Type.ENTITY || t instanceof Monster)) {
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
