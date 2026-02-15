package chneau.autotool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;

public class AutoFish implements EndTick {
    private static EntityDataAccessor<Boolean> DATA_BITING;
    private int recastTicks = -1;

    static {
        DATA_BITING = fetchDataBiting();
    }

    @SuppressWarnings("unchecked")
    private static EntityDataAccessor<Boolean> fetchDataBiting() {
        try {
            // Using official Mojang mappings names
            Field field = FishingHook.class.getDeclaredField("DATA_BITING");
            field.setAccessible(true);
            return (EntityDataAccessor<Boolean>) field.get(null);
        } catch (Exception e) {
            Main.LOGGER.error("Failed to access DATA_BITING in FishingHook", e);
            return null;
        }
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(Minecraft client) {
        if (!ConfigManager.getConfig().autoFish) return;

        var player = client.player;
        if (player == null || !Util.isCurrentPlayer(player)) return;

        boolean holdingRod = player.getMainHandItem().is(Items.FISHING_ROD) || 
                             player.getOffhandItem().is(Items.FISHING_ROD);
        
        if (!holdingRod) {
            recastTicks = -1;
            return;
        }

        if (recastTicks > 0) {
            recastTicks--;
            if (recastTicks == 0) {
                use(client);
            }
            return;
        }

        FishingHook hook = player.fishing;
        if (hook != null && DATA_BITING != null) {
            boolean isBiting = hook.getEntityData().get(DATA_BITING);
            if (isBiting) {
                use(client);
                recastTicks = 40; // Recast after 2 seconds to be safe
            }
        }
    }

    private void use(Minecraft client) {
        if (client.gameMode == null || client.player == null) return;
        InteractionHand hand = client.player.getMainHandItem().is(Items.FISHING_ROD) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        client.gameMode.useItem(client.player, hand);
    }
}
