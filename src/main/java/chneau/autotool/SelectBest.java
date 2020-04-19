package chneau.autotool;

import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * SelectBest
 */
public class SelectBest implements Select {
    private static final String ATTACK_DAMAGE_ID = EntityAttributes.ATTACK_DAMAGE.getId();
    private static final String ATTACK_SPEED_ID = EntityAttributes.ATTACK_SPEED.getId();

    @Override
    public int selectTool(PlayerInventory inv, BlockState bState) {
        float bestSpeed = 1;
        int bestIndex = -1;
        Item targetItem = bState.getBlock().asItem();
        ItemStack itemStack = new ItemStack(targetItem);
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            Item item = inv.main.get(i).getItem();
            float speed = item.getMiningSpeed(itemStack, bState);
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
            Multimap<String, EntityAttributeModifier> mm = item.getModifiers(EquipmentSlot.MAINHAND);
            double atkDmg = 1 + mm.get(ATTACK_DAMAGE_ID).stream().map(x -> x.getAmount()).reduce(0d, (a, b) -> a + b);
            double atkSpd = 4 + mm.get(ATTACK_SPEED_ID).stream().map(x -> x.getAmount()).reduce(0d, (a, b) -> a + b);
            double dps = atkDmg * atkSpd;
            if (bestDPS < dps) {
                bestDPS = dps;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}
