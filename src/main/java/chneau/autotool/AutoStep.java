package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AutoStep implements EndTick {

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player)) return;

        var mode = ConfigManager.getConfig().autoStep;
        var stepHeightAttr = player.getAttribute(Attributes.STEP_HEIGHT);
        
        if (stepHeightAttr != null) {
            double targetHeight = mode == Config.StepMode.ON ? 1.0 : 0.6;
            if (stepHeightAttr.getBaseValue() != targetHeight) {
                stepHeightAttr.setBaseValue(targetHeight);
            }
        }
    }
}
