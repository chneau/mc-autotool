package chneau.autotool;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import java.util.function.ToDoubleFunction;
public interface Select {
	int HOTBAR_SIZE = Inventory.getSelectionSize();
	int selectTool(Inventory inventory, BlockState bState);
	int selectWeapon(Inventory inventory);
	int selectAnyTool(Inventory inventory, BlockState bState);
	int selectAnyWeapon(Inventory inventory);
	static int find(Inventory inventory, int limit, ToDoubleFunction<ItemStack> scorer, double minScore) {
		var bestScore = minScore;
		var bestIndex = -1;
		for (var i = 0; i < limit; i++) {
			var score = scorer.applyAsDouble(inventory.getItem(i));
			if (score > bestScore) {
				bestScore = score;
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	static Select best() {
		return new Select() {
			@Override
			public int selectTool(Inventory inv, BlockState bs) {
				return find(inv, HOTBAR_SIZE, s -> s.getDestroySpeed(bs), 1.0);
			}
			@Override
			public int selectAnyTool(Inventory inv, BlockState bs) {
				return find(inv, inv.getContainerSize(), s -> s.getDestroySpeed(bs), 1.0);
			}
			@Override
			public int selectWeapon(Inventory inv) {
				return find(inv, HOTBAR_SIZE, s -> Util.getWeaponDamage(s) * Util.getWeaponSpeed(s), 4.0);
			}
			@Override
			public int selectAnyWeapon(Inventory inv) {
				return find(inv, inv.getContainerSize(), s -> Util.getWeaponDamage(s) * Util.getWeaponSpeed(s), 4.0);
			}
		};
	}
	static Select first() {
		return new Select() {
			@Override
			public int selectTool(Inventory inv, BlockState bs) {
				return selectAnyTool(inv, bs, HOTBAR_SIZE);
			}
			@Override
			public int selectAnyTool(Inventory inv, BlockState bs) {
				return selectAnyTool(inv, bs, inv.getContainerSize());
			}
			private int selectAnyTool(Inventory inv, BlockState bs, int limit) {
				return find(inv, limit, s -> (s.is(ItemTags.PICKAXES) || s.is(ItemTags.AXES) || s.is(ItemTags.SHOVELS)
						|| s.is(ItemTags.HOES)) && s.getDestroySpeed(bs) > 1 ? 2.0 : 0.0, 1.0);
			}
			@Override
			public int selectWeapon(Inventory inv) {
				return selectAnyWeapon(inv, HOTBAR_SIZE);
			}
			@Override
			public int selectAnyWeapon(Inventory inv) {
				return selectAnyWeapon(inv, inv.getContainerSize());
			}
			private int selectAnyWeapon(Inventory inv, int limit) {
				return find(inv, limit, s -> s.is(ItemTags.SWORDS) ? 2.0 : 0.0, 1.0);
			}
		};
	}
}
