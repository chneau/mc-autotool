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
	private long lastAttack = 0, cachedDelay = 0;
	private ItemStack lastStack = ItemStack.EMPTY;
	@Override
	public void onEndTick(Minecraft client) {
		var mode = config().autoAttack;
		if (mode == Config.AttackMode.OFF)
			return;
		var p = client.player;
		if (p.getInventory() == null || client.level == null)
			return;
		var hand = p.getMainHandItem();
		if (mode == Config.AttackMode.SWORD && !hand.is(ItemTags.SWORDS))
			return;
		var target = (client.hitResult instanceof EntityHitResult ehr) ? ehr.getEntity() : null;
		if (target == null)
			for (var e : client.level.entitiesForRendering())
				if (e instanceof Monster m && m.isAlive() && m.distanceTo(p) <= 3.5) {
					target = m;
					break;
				}
		if (target instanceof LivingEntity le && le.isAlive()
				&& (client.hitResult != null && client.hitResult.getType() == HitResult.Type.ENTITY
						|| target instanceof Monster)) {
			if (!Util.areItemsEqual(hand, lastStack)) {
				lastStack = hand.copy();
				cachedDelay = (long) (1000.0 / Util.getWeaponSpeed(hand));
			}
			var now = System.currentTimeMillis();
			if (now - lastAttack >= cachedDelay && client.gameMode != null) {
				client.gameMode.attack(p, target);
				p.resetAttackStrengthTicker();
				p.swing(InteractionHand.MAIN_HAND);
				lastAttack = now;
			}
		}
	}
}
