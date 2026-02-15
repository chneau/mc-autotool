package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class AutoAttack implements EndTick {
    private long lastAttack = System.currentTimeMillis();
    private ItemStack lastStack = ItemStack.EMPTY;
    private long cachedDelay = 0;

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        var mode = ConfigManager.getConfig().autoAttack;
        if (mode == Config.AttackMode.OFF)
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
            
            if (mode == Config.AttackMode.SWORD && !itemStackMainHand.is(ItemTags.SWORDS))
                return;
            
            var now = System.currentTimeMillis();
            
            if (!Util.areItemsEqual(itemStackMainHand, lastStack)) {
                lastStack = itemStackMainHand.copy();
                var modifiers = itemStackMainHand.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                var atkSpd = modifiers.compute(Attributes.ATTACK_SPEED, 4.0, EquipmentSlot.MAINHAND);
                cachedDelay = (long) (1000.0 / atkSpd);
            }
            
            if (now - lastAttack < cachedDelay)
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
