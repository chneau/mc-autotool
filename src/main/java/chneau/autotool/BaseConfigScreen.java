package chneau.autotool;

import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.function.Consumer;

public abstract class BaseConfigScreen extends OptionsSubScreen {

    public BaseConfigScreen(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    protected <T extends Enum<T>> OptionInstance<T> createEnumOption(String name, T[] values, T currentValue, Consumer<T> setter) {
        return new OptionInstance<>(
                name,
                OptionInstance.noTooltip(),
                (caption, value) -> Component.literal(value.name().replace('_', ' ')),
                new OptionInstance.Enum<>(Arrays.asList(values), Codec.INT.xmap(i -> values[i], Enum::ordinal)),
                currentValue,
                setter
        );
    }

    protected OptionInstance<Integer> createIntOption(String key, int currentValue, Consumer<Integer> setter) {
        return new OptionInstance<>(
            key,
            OptionInstance.noTooltip(),
            (caption, value) -> CommonComponents.optionNameValue(caption, Component.literal(value == 0 ? "Off" : value.toString())),
            new OptionInstance.IntRange(0, 5),
            Codec.INT,
            currentValue,
            setter
        );
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        AutoStep.update();
        this.minecraft.setScreen(this.lastScreen);
    }
}
