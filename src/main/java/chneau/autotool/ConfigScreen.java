package chneau.autotool;

import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ConfigScreen extends BaseConfigScreen {

    public ConfigScreen(Screen parent, Options options) {
        super(parent, options, Component.literal("Autotool Config"));
    }

    @Override
    protected void addOptions() {
        Config config = ConfigManager.getConfig();

        this.list.addSmall(
            createEnumOption("Auto Attack", Config.AttackMode.values(), config.autoAttack, v -> config.autoAttack = v),
            createEnumOption("Auto Farm", Config.FarmMode.values(), config.autoFarm, v -> config.autoFarm = v)
        );
        this.list.addSmall(
            createEnumOption("Auto Refill", Config.RefillMode.values(), config.autoRefill, v -> config.autoRefill = v),
            createEnumOption("Auto Sprint", Config.SprintMode.values(), config.autoSprint, v -> config.autoSprint = v)
        );
        this.list.addSmall(
            createEnumOption("Auto Eat", Config.EatMode.values(), config.autoEat, v -> config.autoEat = v),
            createEnumOption("Auto Sort", Config.SortMode.values(), config.autoSort, v -> config.autoSort = v)
        );
        this.list.addSmall(
            createEnumOption("Auto Armor", Config.ArmorMode.values(), config.autoArmor, v -> config.autoArmor = v),
            createEnumOption("Auto Fish", Config.FishMode.values(), config.autoFish, v -> config.autoFish = v)
        );
        this.list.addSmall(
            createEnumOption("Auto Deposit", Config.DepositMode.values(), config.autoDeposit, v -> config.autoDeposit = v),
            createEnumOption("Auto Swap", Config.Strategy.values(), config.autoSwap, v -> config.autoSwap = v)
        );
        this.list.addSmall(
            createEnumOption("Auto Step", Config.StepMode.values(), config.autoStep, v -> config.autoStep = v).createButton(this.options),
            Button.builder(Component.literal("Targeting Settings..."), (button) -> {
                this.minecraft.setScreen(new TargetConfigScreen(this, this.options));
            }).build()
        );
    }

    @Override
    protected void addFooter() {
        LinearLayout linearLayout = LinearLayout.horizontal().spacing(8);
        
        linearLayout.addChild(Button.builder(Component.literal("Reset to Defaults"), (button) -> {
            ConfigManager.getConfig().resetToDefault();
            ConfigManager.save();
            AutoStep.update();
            this.minecraft.setScreen(new ConfigScreen(this.lastScreen, this.options));
        }).width(150).build());

        linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.onClose();
        }).width(150).build());

        this.layout.addToFooter(linearLayout);
    }
}
