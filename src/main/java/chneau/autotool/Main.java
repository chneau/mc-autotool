package chneau.autotool;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mc-autotool");
    private static KeyMapping configKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing mc-autotool...");
        ConfigManager.load();
        Throttler.register();
        (new AutoSwap()).register();
        (new AutoRefill()).register();
        (new AutoFarm()).register();
        (new AutoAttack()).register();
        (new AutoSprint()).register();
        (new AutoEat()).register();
        (new AutoSort()).register();
        (new AutoArmor()).register();
        (new AutoFish()).register();
        (new AutoTarget()).register();
        (new AutoStep()).register();
        (new AutoDeposit()).register();

        configKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.mc-autotool.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKey.consumeClick()) {
                var window = client.getWindow();
                boolean ctrl = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL) ||
                               InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
                boolean shift = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                                InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
                
                if (ctrl && shift) {
                    client.setScreen(new ConfigScreen(client.screen, client.options));
                }
            }
        });
    }

}
