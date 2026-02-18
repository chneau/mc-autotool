package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
public class AutoAttack implements EndTick {
	private long lastAttack = System.currentTimeMillis();
	private ItemStack lastStack = ItemStack.EMPTY;
	private long cachedDelay = 0;
	public void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> Safe.run("AutoAttack", () -> this.onEndTick(client)));
	}
	@Override
	public void onEndTick(Minecraft client) {
		var mode = ConfigManager.getConfig().autoAttack;
		if (mode == Config.AttackMode.OFF)
			return;
		var player = client.player;
		if (player == null || !Util.isCurrentPlayer(player) || player.getInventory() == null || client.level == null)
			return;
		var inventory = player.getInventory();
		var itemStackMainHand = inventory.getItem(inventory.getSelectedSlot());
		if (mode == Config.AttackMode.SWORD && !itemStackMainHand.is(ItemTags.SWORDS))
			return;
		Entity entityToAttack = null;
		if (client.hitResult != null && client.hitResult.getType() == Type.ENTITY) {
			entityToAttack = ((EntityHitResult) client.hitResult).getEntity();
		} else {
			double minDistance = 3.5; // Reach distance
			for (var entity : client.level.entitiesForRendering()) {
				if (entity instanceof Monster monster && monster.isAlive() && !monster.isInvulnerable()) {
					double dist = monster.distanceTo(player);
					if (dist <= minDistance) {
						minDistance = dist;
						entityToAttack = monster;
					}
				}
			}
		}
		if (entityToAttack instanceof LivingEntity living && living.isAlive() && living.getHealth() > 0) {
			// If we found an entity through proximity (not hitResult), it must be a monster
			if (client.hitResult == null || client.hitResult.getType() != Type.ENTITY) {
				if (!(entityToAttack instanceof Monster)) {
					return;
				}
			}
			var now = System.currentTimeMillis();
			if (!Util.areItemsEqual(itemStackMainHand, lastStack)) {
				lastStack = itemStackMainHand.copy();
				cachedDelay = (long) (1000.0 / Util.getWeaponSpeed(itemStackMainHand));
			}
			if (now - lastAttack < cachedDelay)
				return;
			if (client.gameMode != null) {
				client.gameMode.attack(player, entityToAttack);
				player.resetAttackStrengthTicker();
				player.swing(InteractionHand.MAIN_HAND);
				lastAttack = now;
			}
		}
	}
}
