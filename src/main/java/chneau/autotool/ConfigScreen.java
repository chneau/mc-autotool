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

        this.addRenderableWidget(CycleButton.builder((Config.AttackMode m) -> Component.literal(m.name()), config.autoAttack)
                .withValues(Config.AttackMode.values())
                .create(x, y, 200, 20, Component.literal("Auto Attack"), (button, value) -> config.autoAttack = value));

        this.addRenderableWidget(CycleButton.builder((Config.FarmMode m) -> Component.literal(m.name()), config.autoFarm)
                .withValues(Config.FarmMode.values())
                .create(x, y + 24, 200, 20, Component.literal("Auto Farm"), (button, value) -> config.autoFarm = value));

        this.addRenderableWidget(CycleButton.builder((Config.RefillMode m) -> Component.literal(m.name()), config.autoRefill)
                .withValues(Config.RefillMode.values())
                .create(x, y + 48, 200, 20, Component.literal("Auto Refill"), (button, value) -> config.autoRefill = value));

        this.addRenderableWidget(CycleButton.builder((Config.SprintMode m) -> Component.literal(m.name()), config.autoSprint)
                .withValues(Config.SprintMode.values())
                .create(x, y + 72, 200, 20, Component.literal("Auto Sprint"), (button, value) -> config.autoSprint = value));

        this.addRenderableWidget(CycleButton.builder((Config.EatMode m) -> Component.literal(m.name()), config.autoEat)
                .withValues(Config.EatMode.values())
                .create(x, y + 96, 200, 20, Component.literal("Auto Eat"), (button, value) -> config.autoEat = value));

        this.addRenderableWidget(CycleButton.builder((Config.Strategy s) -> Component.literal(s.name()), config.autoSwap)
                .withValues(Config.Strategy.values())
                .create(x, y + 120, 200, 20, Component.literal("Auto Swap"), (button, value) -> config.autoSwap = value));

        this.addRenderableWidget(Button.builder(Component.literal("Done"), (button) -> {
            ConfigManager.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(x, y + 154, 200, 20).build());
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
