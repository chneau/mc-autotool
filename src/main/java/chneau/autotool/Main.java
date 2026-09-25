package chneau.autotool;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Stream;
public class Main implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mc-autotool");
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing mc-autotool...");
		ConfigManager.load();
		Throttler.register();
		Stream.of(new AutoSwap(), new AutoRefill(), new AutoFarm(), new AutoAttack(), new AutoSprint(), new AutoEat(),
				new AutoSort(), new AutoArmor(), new AutoFish(), new AutoTarget(), new AutoStep(), new AutoDeposit())
				.forEach(Module::register);
		net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT
				.register((dispatcher, registryAccess) -> {
					var command = net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("autotool")
							.executes(context -> {
								var c = context.getSource().getClient();
//? if >=26.2 {
								c.execute(() -> c.gui.setScreen(new ConfigScreen(c.gui.screen(), c.options)));
//?} else {
/*								c.execute(() -> c.setScreen(new ConfigScreen(c.screen, c.options)));
*///?}
								return 1;
							});
					dispatcher.register(command);
					dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal("at")
							.executes(command.getCommand()));
				});
	}
}

