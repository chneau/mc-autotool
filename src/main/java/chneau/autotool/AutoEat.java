package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
public class AutoEat implements EndTick {
	private long lastActivity = System.currentTimeMillis();
	private int lastSlot = -1;
	private boolean isEating = false;
	private double lastX, lastY, lastZ;
	public void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> Safe.run("AutoEat", () -> this.onEndTick(client)));
	}
	@Override
	public void onEndTick(Minecraft client) {
		var mode = ConfigManager.getConfig().autoEat;
		if (mode == Config.EatMode.OFF) {
			if (isEating)
				stopEating(client);
			return;
		}
		var player = client.player;
		if (player == null || !Util.isCurrentPlayer(player))
			return;
		// Activity check: Detect manual activity efficiently
		boolean active = false;
		// 1. Check for movement
		if (player.getX() != lastX || player.getY() != lastY || player.getZ() != lastZ) {
			active = true;
		}
		lastX = player.getX();
		lastY = player.getY();
		lastZ = player.getZ();
		// 2. Check for common action keys
		if (!active) {
			if (client.options.keyAttack.isDown() || (client.options.keyUse.isDown() && !isEating)
					|| client.options.keyJump.isDown() || client.options.keyShift.isDown()
					|| client.options.keySprint.isDown()) {
				active = true;
			}
		}
		// 3. Check if any screen is open
		if (!active && client.screen != null) {
			active = true;
		}
		if (active) {
			lastActivity = System.currentTimeMillis();
			if (isEating)
				stopEating(client);
			return;
		}
		if (System.currentTimeMillis() - lastActivity < 1000) {
			return;
		}
		if (isEating) {
			if (player.isUsingItem()) {
				return; // Still busy eating
			}
			// If we were eating but stopped, check if we finished or were interrupted
			if (!shouldEat(player, mode)) {
				stopEating(client);
				return;
			}
			// If we still need to eat, we'll continue below (isEating will be set to true
			// again)
			isEating = false;
		}
		if (shouldEat(player, mode)) {
			int hunger = player.getFoodData().getFoodLevel();
			int foodSlot = findBestFood(player.getInventory(), hunger, mode == Config.EatMode.SMART);
			if (foodSlot != -1) {
				startEating(client, foodSlot);
			} else if (isEating) {
				stopEating(client);
			}
		}
	}
	private boolean shouldEat(net.minecraft.world.entity.player.Player player, Config.EatMode mode) {
		int hunger = player.getFoodData().getFoodLevel();
		float health = player.getHealth();
		float maxHealth = player.getMaxHealth();
		if (mode == Config.EatMode.HUNGER && hunger < 20)
			return true;
		if (mode == Config.EatMode.HEALTH && health < maxHealth)
			return true;
		if (mode == Config.EatMode.SMART) {
			if (hunger <= 14)
				return true;
			if (health < maxHealth && hunger < 20)
				return true;
		}
		return false;
	}
	private int findBestFood(Inventory inventory, int currentHunger, boolean smart) {
		int bestSlot = -1;
		int needed = 20 - currentHunger;
		int bestDifference = Integer.MAX_VALUE;
		for (int i = 0; i < 9; i++) { // Check hotbar
			ItemStack stack = inventory.getItem(i);
			var food = stack.get(DataComponents.FOOD);
			if (food != null) {
				int value = food.nutrition();
				if (smart) {
					// Don't eat if it heals way more than needed, unless we are very hungry
					if (currentHunger > 10 && value > needed + 4)
						continue;
					int diff = Math.abs(value - needed);
					if (diff < bestDifference) {
						bestDifference = diff;
						bestSlot = i;
					}
				} else {
					if (bestSlot == -1 || value > inventory.getItem(bestSlot).get(DataComponents.FOOD).nutrition()) {
						bestSlot = i;
					}
				}
			}
		}
		return bestSlot;
	}
	private void startEating(Minecraft client, int slot) {
		var player = client.player;
		if (player == null)
			return;
		if (lastSlot == -1) {
			lastSlot = player.getInventory().getSelectedSlot();
		}
		if (player.getInventory().getSelectedSlot() != slot) {
			player.getInventory().setSelectedSlot(slot);
		}
		client.options.keyUse.setDown(true);
		isEating = true;
	}
	private void stopEating(Minecraft client) {
		client.options.keyUse.setDown(false);
		if (lastSlot != -1 && client.player != null) {
			client.player.getInventory().setSelectedSlot(lastSlot);
		}
		lastSlot = -1;
		isEating = false;
	}
}
