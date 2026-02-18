package chneau.autotool;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import java.util.HashSet;

public class AutoDeposit extends BaseModule {
	public AutoDeposit() {
		super("AutoDeposit");
	}

	@Override
	public void register() {
		ScreenEvents.AFTER_INIT.register(Safe.containerScreen(name, (client, screen, w, h) -> {
			var mode = config().autoDeposit;
			if (mode == Config.DepositMode.OFF)
				return;
			boolean furnace = screen.getMenu() instanceof AbstractFurnaceMenu;
			if ((furnace && (mode == Config.DepositMode.FURNACE || mode == Config.DepositMode.ALL))
					|| (!furnace && (mode == Config.DepositMode.CHEST || mode == Config.DepositMode.ALL)))
				Util.addButton(screen, screen, "D", "Deposit Items", 20,
						() -> Safe.run(name, () -> handle(client, furnace)));
		}));
	}

	private void handle(Minecraft client, boolean furnace) {
		var menu = client.player.containerMenu;
		var items = new HashSet<>();
		for (var s : menu.slots)
			if (!(s.container instanceof Inventory) && !s.getItem().isEmpty())
				items.add(s.getItem().getItem());
		for (int i = 0; i < menu.slots.size(); i++) {
			var s = menu.getSlot(i);
			if (s.container instanceof Inventory && !s.getItem().isEmpty()
					&& (furnace || items.contains(s.getItem().getItem())))
				Util.quickMove(client, menu.containerId, i);
		}
	}
}
