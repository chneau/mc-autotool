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

public class TargetConfigScreen extends BaseConfigScreen {

    public TargetConfigScreen(Screen parent, Options options) {
        super(parent, options, Component.literal("Targeting Settings"));
    }

    @Override
    protected void addOptions() {
        Config config = ConfigManager.getConfig();

        this.list.addSmall(
            createIntOption("Target Monsters", config.targetMonster, v -> config.targetMonster = v),
            createIntOption("Target Passives", config.targetPassive, v -> config.targetPassive = v)
        );
        this.list.addSmall(
            createIntOption("Target Players", config.targetPlayer, v -> config.targetPlayer = v),
            createIntOption("Target Diamond", config.targetDiamond, v -> config.targetDiamond = v)
        );
        this.list.addSmall(
            createIntOption("Target Emerald", config.targetEmerald, v -> config.targetEmerald = v),
            createIntOption("Target Gold", config.targetGold, v -> config.targetGold = v)
        );
        this.list.addSmall(
            createIntOption("Target Iron", config.targetIron, v -> config.targetIron = v),
            createIntOption("Target Debris", config.targetDebris, v -> config.targetDebris = v)
        );
        this.list.addBig(createEnumOption("HUD Position", Config.HudPosition.values(), config.targetHudPosition, v -> config.targetHudPosition = v));
    }

    @Override
    protected void addFooter() {
        LinearLayout linearLayout = LinearLayout.horizontal().spacing(8);
        
        linearLayout.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.onClose();
        }).width(150).build());

        this.layout.addToFooter(linearLayout);
    }
}
