package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class Autoattack implements EndTick {
    private static final long DEFAULT_ATTACK_DELAY_MS = 625; // 1.6 attack speed
    private long lastAttack = System.currentTimeMillis();

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        if (!ConfigManager.getConfig().autoAttackEnabled)
            return;
        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player))
            return;
        if (client.hitResult == null || player.getInventory() == null)
            return;
        var inventory = player.getInventory();
        var itemStackMainHand = inventory.getItem(inventory.getSelectedSlot());
        if (client.hitResult.getType() == Type.ENTITY) {
            var entity = ((EntityHitResult) client.hitResult).getEntity();
            if (entity instanceof LivingEntity living && living.getHealth() <= 0)
                return;
            if (!(itemStackMainHand.is(ItemTags.SWORDS)))
                return;
            
            var now = System.currentTimeMillis();
            var modifiers = itemStackMainHand.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            var atkSpd = modifiers.compute(Attributes.ATTACK_SPEED, 4.0, EquipmentSlot.MAINHAND);
            var delay = (long) (1000.0 / atkSpd);
            
            // If the delay is unusually high (e.g. 4.0 which results in 250ms), we might want to respect a minimum or the config
            // But here we'll just show we use the config if atkSpd is default/missing or as a multiplier maybe?
            // Actually, let's just use the config if it's different from default.
            if (ConfigManager.getConfig().defaultAttackDelayMs != 625 && atkSpd == 4.0) {
                delay = ConfigManager.getConfig().defaultAttackDelayMs;
            }

            if (now - lastAttack < delay)
                return;
            if (client.gameMode != null) {
                client.gameMode.attack(player, entity);
                player.resetAttackStrengthTicker();
                player.swing(InteractionHand.MAIN_HAND);
                lastAttack = now;
            }
        }
    }
}
