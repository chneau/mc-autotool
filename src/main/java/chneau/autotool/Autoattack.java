package chneau.autotool;

import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;

/**
 * Autotool
 */
public class Autoattack implements ClientTickCallback {
    private long lastAttack = System.currentTimeMillis();

    public void register() {
        ClientTickCallback.EVENT.register(this);
    }

    @Override
    public void tick(MinecraftClient c) {
        ClientPlayerEntity player = c.player;
        if (player == null || c.hitResult == null || player.inventory == null)
            return;
        Item itemMainHand = player.inventory.main.get(player.inventory.selectedSlot).getItem();
        if (c.hitResult.getType() == Type.ENTITY) {
            if (itemMainHand instanceof SwordItem == false)
                return;
            long now = System.currentTimeMillis();
            if (now - lastAttack < 625)
                return;
            c.interactionManager.attackEntity(player, ((EntityHitResult) c.hitResult).getEntity());
            player.resetLastAttackedTicks();
            player.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
        }
    }
}
