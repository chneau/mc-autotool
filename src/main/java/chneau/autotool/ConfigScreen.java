package chneau.autotool;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.function.Consumer;

public class ConfigScreen extends OptionsSubScreen {

    public ConfigScreen(Screen parent, Options options) {
        super(parent, options, Component.literal("Autotool Config"));
    }

    @Override
    protected void addOptions() {
        Config config = ConfigManager.getConfig();

        this.list.addBig(createEnumOption("Auto Attack", Config.AttackMode.values(), config.autoAttack, v -> config.autoAttack = v));
        this.list.addBig(createEnumOption("Auto Farm", Config.FarmMode.values(), config.autoFarm, v -> config.autoFarm = v));
        this.list.addBig(createEnumOption("Auto Refill", Config.RefillMode.values(), config.autoRefill, v -> config.autoRefill = v));
        this.list.addBig(createEnumOption("Auto Sprint", Config.SprintMode.values(), config.autoSprint, v -> config.autoSprint = v));
        this.list.addBig(createEnumOption("Auto Eat", Config.EatMode.values(), config.autoEat, v -> config.autoEat = v));
        this.list.addBig(createEnumOption("Auto Sort", Config.SortMode.values(), config.autoSort, v -> config.autoSort = v));
        this.list.addBig(createEnumOption("Auto Armor", Config.ArmorMode.values(), config.autoArmor, v -> config.autoArmor = v));
        this.list.addBig(createEnumOption("Auto Swap", Config.Strategy.values(), config.autoSwap, v -> config.autoSwap = v));
    }

    private <T extends Enum<T>> OptionInstance<T> createEnumOption(String name, T[] values, T currentValue, Consumer<T> setter) {
        return new OptionInstance<>(
                name,
                OptionInstance.noTooltip(),
                (caption, value) -> Component.literal(name + ": " + value.name()),
                new OptionInstance.Enum<>(Arrays.asList(values), Codec.INT.xmap(i -> values[i], Enum::ordinal)),
                currentValue,
                setter
        );
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        this.minecraft.setScreen(this.lastScreen);
    }
}
