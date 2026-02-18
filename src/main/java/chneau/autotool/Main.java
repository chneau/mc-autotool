package chneau.autotool;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
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
		configKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.mc-autotool.config",
				InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, KeyMapping.Category.MISC));
		ClientTickEvents.END_CLIENT_TICK.register(Safe.tick("Main.ConfigKey", c -> {
			while (configKey.consumeClick()) {
				var w = c.getWindow();
				if ((InputConstants.isKeyDown(w, GLFW.GLFW_KEY_LEFT_CONTROL)
						|| InputConstants.isKeyDown(w, GLFW.GLFW_KEY_RIGHT_CONTROL))
						&& (InputConstants.isKeyDown(w, GLFW.GLFW_KEY_LEFT_SHIFT)
								|| InputConstants.isKeyDown(w, GLFW.GLFW_KEY_RIGHT_SHIFT)))
					c.setScreen(new ConfigScreen(c.screen, c.options));
			}
		}));
	}
}
