package chneau.autotool;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            Config config = ConfigManager.getConfig();
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("Autotool Config"));

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Auto Attack"), config.autoAttackEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.autoAttackEnabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Auto Farm"), config.autoFarmEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.autoFarmEnabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Auto Tool"), config.autoToolEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.autoToolEnabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Auto Refill"), config.autoRefillEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.autoRefillEnabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startLongField(Component.literal("Default Attack Delay (ms)"), config.defaultAttackDelayMs)
                    .setDefaultValue(625L)
                    .setSaveConsumer(newValue -> config.defaultAttackDelayMs = newValue)
                    .build());

            builder.setSavingRunnable(ConfigManager::save);

            return builder.build();
        };
    }
}
