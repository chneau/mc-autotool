package chneau.autotool;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Stream;
public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mc-autotool");
	private static KeyMapping configKey;
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing mc-autotool...");
		ConfigManager.load();
		Throttler.register();
		Stream.of(new AutoSwap(), new AutoRefill(), new AutoFarm(), new AutoAttack(), new AutoSprint(), new AutoEat(),
				new AutoSort(), new AutoArmor(), new AutoFish(), new AutoTarget(), new AutoStep(), new AutoDeposit())
				.forEach(Module::register);
		configKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.mc-autotool.config",
				InputConstants.Type.KEYBOARD, InputConstants.KEY_O, KeyMapping.Category.MISC));
		net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT
				.register((dispatcher, registryAccess) -> {
					var command = net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("autotool")
							.executes(context -> {
								var c = context.getSource().getClient();
								c.execute(() -> c.gui.setScreen(new ConfigScreen(c.gui.screen(), c.options)));
								return 1;
							});
					dispatcher.register(command);
					dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("at")
							.executes(command.getCommand()));
				});
		ClientTickEvents.END_CLIENT_TICK.register(Safe.tick("Main.ConfigKey", c -> {
			while (configKey.consumeClick()) {
				if ((InputConstants.isKeyDown(InputConstants.KEY_LCONTROL)
						|| InputConstants.isKeyDown(InputConstants.KEY_RCONTROL))
						&& (InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)
								|| InputConstants.isKeyDown(InputConstants.KEY_RSHIFT)))
					c.gui.setScreen(new ConfigScreen(c.gui.screen(), c.options));
			}
		}));
	}
}
