package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
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
    public void onEndTick(MinecraftClient c) {
        ClientPlayerEntity p = c.player;
        if (p == null || c.crosshairTarget == null || p.getInventory() == null)
            return;
        if (!Util.isCurrentPlayer(p))
            return;
        Item itemMainHand = p.getInventory().main.get(p.getInventory().selectedSlot).getItem();
        if (c.crosshairTarget.getType() == Type.ENTITY) {
            if (itemMainHand instanceof SwordItem == false)
                return;
            long now = System.currentTimeMillis();
            if (now - lastAttack < 625)
                return;
            c.interactionManager.attackEntity(p, ((EntityHitResult) c.crosshairTarget).getEntity());
            p.resetLastAttackedTicks();
            p.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
        }
    }
}
