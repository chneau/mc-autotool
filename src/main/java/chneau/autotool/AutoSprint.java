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
        if (!ConfigManager.getConfig().autoSprintEnabled)
            return;
        
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player))
            return;

        if (player.horizontalCollision || player.isDescending() || player.isUsingItem())
            return;

        if (player.getFoodData().getFoodLevel() <= 6 && !player.getAbilities().mayfly)
            return;

        if (player.input.hasForwardImpulse() && !player.isSprinting()) {
            player.setSprinting(true);
        }
    }
}
