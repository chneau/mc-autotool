package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class Autoattack implements EndTick {
    private long lastAttack = System.currentTimeMillis();

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player))
            return;
        if (client.hitResult == null || player.getInventory() == null)
            return;
        var itemStackMainHand = player.getInventory().getItem(player.getInventory().getSelectedSlot());
        if (client.hitResult.getType() == Type.ENTITY) {
            if (!(itemStackMainHand.is(ItemTags.SWORDS)))
                return;
            var now = System.currentTimeMillis();
            if (now - lastAttack < 625)
                return;
            if (client.gameMode != null) {
                client.gameMode.attack(player, ((EntityHitResult) client.hitResult).getEntity());
                player.resetAttackStrengthTicker();
                player.swing(InteractionHand.MAIN_HAND);
                lastAttack = now;
            }
        }
    }
}
