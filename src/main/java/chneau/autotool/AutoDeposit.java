package chneau.autotool;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import java.util.HashSet;
public class AutoDeposit extends BaseModule implements Safe.ContainerScreenInit {
	@Override
	public void afterInit(Minecraft c, net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?> s, int w,
			int h) {
		var m = config().autoDeposit;
		if (m == Config.DepositMode.OFF)
			return;
		boolean f = s.getMenu() instanceof AbstractFurnaceMenu;
		if ((f && (m == Config.DepositMode.FURNACE || m == Config.DepositMode.ALL))
				|| (!f && (m == Config.DepositMode.CHEST || m == Config.DepositMode.ALL)))
			Util.addButton(s, s, "D", "Deposit Items", 20, () -> Safe.run(name, () -> handle(c, f)));
	}
	private void handle(Minecraft c, boolean f) {
		var m = c.player.containerMenu;
		var items = new HashSet<>();
		for (var s : m.slots)
			if (!(s.container instanceof Inventory) && !s.getItem().isEmpty())
				items.add(s.getItem().getItem());
		for (int i = 0; i < m.slots.size(); i++) {
			var s = m.getSlot(i);
			if (s.container instanceof Inventory && !s.getItem().isEmpty()
					&& (f || items.contains(s.getItem().getItem())))
				Util.quickMove(c, m.containerId, i);
		}
	}
}
