package chneau.autotool;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.literal("Autotool Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        Config config = ConfigManager.getConfig();
        int y = this.height / 4;
        int x = this.width / 2 - 100;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.autoAttackEnabled)
                .create(x, y, 200, 20, Component.literal("Auto Attack"), (button, value) -> config.autoAttackEnabled = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(config.autoFarmEnabled)
                .create(x, y + 24, 200, 20, Component.literal("Auto Farm"), (button, value) -> config.autoFarmEnabled = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(config.autoSwapEnabled)
                .create(x, y + 48, 200, 20, Component.literal("Auto Swap"), (button, value) -> config.autoSwapEnabled = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(config.autoRefillEnabled)
                .create(x, y + 72, 200, 20, Component.literal("Auto Refill"), (button, value) -> config.autoRefillEnabled = value));

        this.addRenderableWidget(CycleButton.builder((Config.Strategy s) -> Component.literal(s.name()), config.strategy)
                .withValues(Config.Strategy.values())
                .create(x, y + 96, 200, 20, Component.literal("Strategy"), (button, value) -> config.strategy = value));

        this.addRenderableWidget(Button.builder(Component.literal("Done"), (button) -> {
            ConfigManager.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(x, y + 130, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        this.minecraft.setScreen(this.parent);
    }
}
