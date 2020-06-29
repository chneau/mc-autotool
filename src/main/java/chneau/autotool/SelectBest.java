package chneau.autotool;

import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SelectBest implements Select {
    @Override
    public int selectTool(PlayerInventory inv, BlockState bState) {
        float bestSpeed = 1;
        int bestIndex = -1;
        Item targetItem = bState.getBlock().asItem();
        ItemStack itemStack = new ItemStack(targetItem);
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            Item item = inv.main.get(i).getItem();
            float speed = item.getMiningSpeedMultiplier(itemStack, bState);
            if (bestSpeed < speed) {
                bestSpeed = speed;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    @Override
    public int selectWeapon(PlayerInventory inv) {
        double bestDPS = 4;
        int bestIndex = -1;
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            Item item = inv.main.get(i).getItem();
            Multimap<EntityAttribute, EntityAttributeModifier> mm = item.getAttributeModifiers(EquipmentSlot.MAINHAND);
            double atkDmg = 1 + mm.get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream().map(x -> x.getValue())
                    .reduce(0d, (a, b) -> a + b);
            double atkSpd = 4 + mm.get(EntityAttributes.GENERIC_ATTACK_SPEED).stream().map(x -> x.getValue()).reduce(0d,
                    (a, b) -> a + b);
            double dps = atkDmg * atkSpd;
            if (bestDPS < dps) {
                bestDPS = dps;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}
