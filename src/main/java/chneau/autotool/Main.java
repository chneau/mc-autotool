package chneau.autotool;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mc-autotool");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing mc-autotool...");
        ConfigManager.load();
        (new Autotool()).register();
        (new Autofarm()).register();
        (new Autoattack()).register();
    }

}
