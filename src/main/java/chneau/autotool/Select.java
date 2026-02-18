package chneau.autotool;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import java.util.function.ToDoubleFunction;
public interface Select {
	int HOTBAR_SIZE = Inventory.getSelectionSize();
	int selectTool(Inventory inv, BlockState bs);
	int selectWeapon(Inventory inv);
	int selectAnyTool(Inventory inv, BlockState bs);
	int selectAnyWeapon(Inventory inv);
	static int find(Inventory inv, int limit, ToDoubleFunction<ItemStack> scorer, double min) {
		double best = min;
		int idx = -1;
		for (int i = 0; i < limit; i++) {
			double s = scorer.applyAsDouble(inv.getItem(i));
			if (s > best) {
				best = s;
				idx = i;
			}
		}
		return idx;
	}
	static Select best() {
		return new Select() {
			public int selectTool(Inventory inv, BlockState bs) {
				return find(inv, HOTBAR_SIZE, s -> s.getDestroySpeed(bs), 1.0);
			}
			public int selectAnyTool(Inventory inv, BlockState bs) {
				return find(inv, inv.getContainerSize(), s -> s.getDestroySpeed(bs), 1.0);
			}
			public int selectWeapon(Inventory inv) {
				return find(inv, HOTBAR_SIZE, s -> Util.getWeaponDamage(s) * Util.getWeaponSpeed(s), 4.0);
			}
			public int selectAnyWeapon(Inventory inv) {
				return find(inv, inv.getContainerSize(), s -> Util.getWeaponDamage(s) * Util.getWeaponSpeed(s), 4.0);
			}
		};
	}
	static Select first() {
		return new Select() {
			public int selectTool(Inventory inv, BlockState bs) {
				return selectAnyTool(inv, bs, HOTBAR_SIZE);
			}
			public int selectAnyTool(Inventory inv, BlockState bs) {
				return selectAnyTool(inv, bs, inv.getContainerSize());
			}
			private int selectAnyTool(Inventory inv, BlockState bs, int lim) {
				return find(inv, lim, s -> (s.is(ItemTags.PICKAXES) || s.is(ItemTags.AXES) || s.is(ItemTags.SHOVELS)
						|| s.is(ItemTags.HOES)) && s.getDestroySpeed(bs) > 1 ? 2.0 : 0.0, 1.0);
			}
			public int selectWeapon(Inventory inv) {
				return selectAnyWeapon(inv, HOTBAR_SIZE);
			}
			public int selectAnyWeapon(Inventory inv) {
				return selectAnyWeapon(inv, inv.getContainerSize());
			}
			private int selectAnyWeapon(Inventory inv, int lim) {
				return find(inv, lim, s -> s.is(ItemTags.SWORDS) ? 2.0 : 0.0, 1.0);
			}
		};
	}
}
