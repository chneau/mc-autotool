package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;

public class AutoSprint implements EndTick {

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        var mode = ConfigManager.getConfig().autoSprint;
        if (mode == Config.SprintMode.OFF)
            return;
        
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player))
            return;

        if (player.horizontalCollision || player.isDescending() || player.isUsingItem())
            return;

        int foodLevel = player.getFoodData().getFoodLevel();
        int threshold = (mode == Config.SprintMode.HUNGER_50) ? 10 : 6;

        if (foodLevel <= threshold && !player.getAbilities().mayfly)
            return;

        if (player.input.hasForwardImpulse() && !player.isSprinting()) {
            player.setSprinting(true);
        }
    }
}
