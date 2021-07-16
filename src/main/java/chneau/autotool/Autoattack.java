package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;

public class Autoattack implements EndTick {
    private long lastAttack = System.currentTimeMillis();

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player) || client.crosshairTarget == null
                || player.getInventory() == null)
            return;
        var itemMainHand = player.getInventory().main.get(player.getInventory().selectedSlot).getItem();
        if (client.crosshairTarget.getType() == Type.ENTITY) {
            if (itemMainHand instanceof SwordItem == false)
                return;
            var now = System.currentTimeMillis();
            if (now - lastAttack < 625)
                return;
            client.interactionManager.attackEntity(player, ((EntityHitResult) client.crosshairTarget).getEntity());
            player.resetLastAttackedTicks();
            player.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
        }
    }
}
