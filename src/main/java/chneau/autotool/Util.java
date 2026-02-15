package chneau.autotool;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Utility class for common Minecraft client-side operations.
 */
public class Util {
	private Util() {
	}

	/**
	 * Retrieves the BlockPos of the block the player is currently looking at.
	 *
	 * @param client
	 *            The Minecraft client instance.
	 * @return The BlockPos of the targeted block, or null if no block is targeted.
	 */
	public static BlockPos getTargetedBlock(Minecraft client) {
		if (client.hitResult instanceof BlockHitResult bhr) {
			return bhr.getBlockPos();
		}
		return null;
	}

	/**
	 * Checks if the given player is the local client player.
	 *
	 * @param other
	 *            The entity to check.
	 * @return True if the entity is the local client player.
	 */
	public static boolean isCurrentPlayer(net.minecraft.world.entity.Entity other) {
		var instance = Minecraft.getInstance();
		var player = instance.player;
		if (player == null || other == null)
			return false;
		return player.equals(other);
	}

	public static void click(Minecraft client, int containerId, int slotId, int button,
			net.minecraft.world.inventory.ContainerInput type) {
		if (client.gameMode != null && client.player != null) {
			client.gameMode.handleContainerInput(containerId, slotId, button, type, client.player);
		}
	}

	public static void quickMove(Minecraft client, int containerId, int slotId) {
		click(client, containerId, slotId, 0, net.minecraft.world.inventory.ContainerInput.QUICK_MOVE);
	}

	public static void pickup(Minecraft client, int containerId, int slotId) {
		click(client, containerId, slotId, 0, net.minecraft.world.inventory.ContainerInput.PICKUP);
	}

	public static void swap(Minecraft client, int containerId, int fromSlot, int toSlot) {
		if (client.player.inventoryMenu.getSlot(toSlot).getItem().isEmpty()) {
			quickMove(client, containerId, fromSlot);
		} else {
			pickup(client, containerId, fromSlot);
			pickup(client, containerId, toSlot);
			pickup(client, containerId, fromSlot);
		}
	}

	public static boolean areItemsEqual(ItemStack a, ItemStack b) {
		return ItemStack.isSameItemSameComponents(a, b);
	}

	public static double getWeaponDamage(ItemStack stack) {
		var modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		return modifiers.compute(Attributes.ATTACK_DAMAGE, 1.0, EquipmentSlot.MAINHAND);
	}

	public static double getWeaponSpeed(ItemStack stack) {
		var modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		return modifiers.compute(Attributes.ATTACK_SPEED, 4.0, EquipmentSlot.MAINHAND);
	}

	public static double getArmorValue(ItemStack stack, EquipmentSlot slot) {
		var modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		double armor = modifiers.compute(Attributes.ARMOR, 0.0, slot);
		double toughness = modifiers.compute(Attributes.ARMOR_TOUGHNESS, 0.0, slot);
		return armor + toughness;
	}

	public static int getEnchantmentLevelSum(ItemStack stack) {
		var enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		return enchants.keySet().stream().mapToInt(enchants::getLevel).sum();
	}

	public static int getItemWeight(ItemStack stack) {
		if (stack.isEmpty())
			return 100;
		if (stack.is(ItemTags.SWORDS))
			return 0;
		if (stack.is(Items.BOW) || stack.is(Items.CROSSBOW))
			return 1;
		if (stack.is(ItemTags.PICKAXES))
			return 2;
		if (stack.is(ItemTags.AXES))
			return 3;
		if (stack.is(ItemTags.SHOVELS))
			return 4;
		if (stack.is(ItemTags.HOES))
			return 5;
		if (stack.has(DataComponents.FOOD))
			return 6;
		if (stack.getItem() instanceof BlockItem)
			return 7;
		return 8;
	}
}
