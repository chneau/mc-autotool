package chneau.autotool;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.network.chat.Component;
public class Util {
	public record ScreenArea(int left, int top, int width, int height) {
		public int topAbove() {
			return top - 18;
		}
	}
	public static ScreenArea getScreenArea(AbstractContainerScreen<?> s) {
		try {
			var l = AbstractContainerScreen.class.getDeclaredField("leftPos");
			l.setAccessible(true);
			var t = AbstractContainerScreen.class.getDeclaredField("topPos");
			t.setAccessible(true);
			var w = AbstractContainerScreen.class.getDeclaredField("imageWidth");
			w.setAccessible(true);
			var h = AbstractContainerScreen.class.getDeclaredField("imageHeight");
			h.setAccessible(true);
			return new ScreenArea(l.getInt(s), t.getInt(s), w.getInt(s), h.getInt(s));
		} catch (Exception e) {
			return new ScreenArea(0, 0, 0, 0);
		}
	}
	public static void addButton(Screen s, AbstractContainerScreen<?> cs, String l, String t, int r, Runnable a) {
		var area = getScreenArea(cs);
		Screens.getButtons(s)
				.add(Button.builder(Component.literal(l), b -> a.run())
						.bounds(area.left() + area.width() - r, area.topAbove(), 15, 15)
						.tooltip(Tooltip.create(Component.literal(t))).build());
	}
	public static void chatError(String m) {
		var c = Minecraft.getInstance();
		if (c.player != null)
			c.execute(() -> {
				if (c.player != null)
					c.player.displayClientMessage(Component.literal(m).withStyle(net.minecraft.ChatFormatting.RED), false);
			});
	}
	public static void selectSlot(Minecraft c, int s) {
		var p = c.player;
		if (p == null || p.getInventory().getSelectedSlot() == s)
			return;
		p.getInventory().setSelectedSlot(s);
		if (p.connection != null)
			p.connection.send(new net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket(s));
	}
	public static BlockPos getTargetedBlock(Minecraft c) {
		return c.hitResult instanceof BlockHitResult bhr ? bhr.getBlockPos() : null;
	}
	public static boolean isCurrentPlayer(net.minecraft.world.entity.Entity o) {
		return Minecraft.getInstance().player != null && Minecraft.getInstance().player.equals(o);
	}
	public static void click(Minecraft c, int id, int s, int b, net.minecraft.world.inventory.ClickType t) {
		if (c.gameMode != null && c.player != null)
			c.gameMode.handleInventoryMouseClick(id, s, b, t, c.player);
	}
	public static void quickMove(Minecraft c, int id, int s) {
		click(c, id, s, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE);
	}
	public static void pickup(Minecraft c, int id, int s) {
		click(c, id, s, 0, net.minecraft.world.inventory.ClickType.PICKUP);
	}
	public static void swap(Minecraft c, int id, int f, int t) {
		if (c.player.inventoryMenu.getSlot(t).getItem().isEmpty())
			quickMove(c, id, f);
		else {
			pickup(c, id, f);
			pickup(c, id, t);
			pickup(c, id, f);
		}
	}
	public static boolean areItemsEqual(ItemStack a, ItemStack b) {
		return ItemStack.isSameItemSameComponents(a, b);
	}
	public static double getWeaponDamage(ItemStack s) {
		return s.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
				.compute(Attributes.ATTACK_DAMAGE, 1.0, EquipmentSlot.MAINHAND);
	}
	public static double getWeaponSpeed(ItemStack s) {
		return s.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
				.compute(Attributes.ATTACK_SPEED, 4.0, EquipmentSlot.MAINHAND);
	}
	public static double getArmorValue(ItemStack s, EquipmentSlot sl) {
		var m = s.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		return m.compute(Attributes.ARMOR, 0.0, sl) + m.compute(Attributes.ARMOR_TOUGHNESS, 0.0, sl);
	}
	public static int getEnchantmentLevelSum(ItemStack s) {
		var e = s.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		return e.keySet().stream().mapToInt(e::getLevel).sum();
	}
	public static int getItemWeight(ItemStack s) {
		if (s.isEmpty())
			return 100;
		if (s.is(ItemTags.SWORDS))
			return 0;
		if (s.is(Items.BOW) || s.is(Items.CROSSBOW))
			return 1;
		if (s.is(ItemTags.PICKAXES))
			return 2;
		if (s.is(ItemTags.AXES))
			return 3;
		if (s.is(ItemTags.SHOVELS))
			return 4;
		if (s.is(ItemTags.HOES))
			return 5;
		if (s.has(DataComponents.FOOD))
			return 6;
		return s.getItem() instanceof BlockItem ? 7 : 8;
	}
	private Util() {
	}
}
