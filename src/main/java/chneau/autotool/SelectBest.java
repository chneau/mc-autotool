package chneau.autotool;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class SelectBest implements Select {
    @Override
    public int selectTool(Inventory inventory, BlockState blockState) {
        var bestSpeed = 1.;
        var bestIndex = -1;
        var targetItem = blockState.getBlock().asItem();
        var itemStack = new ItemStack(targetItem);
        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var stack = inventory.getItem(i);
            var speed = stack.getDestroySpeed(blockState);
            if (bestSpeed < speed) {
                bestSpeed = speed;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    @Override
    public int selectWeapon(Inventory inventory) {
        var bestDPS = 4.;
        var bestIndex = -1;
        for (var i = 0; i < HOTBAR_SIZE; i++) {
            var stack = inventory.getItem(i);
            
            var modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            var atkDmg = modifiers.compute(Attributes.ATTACK_DAMAGE, 1.0, EquipmentSlot.MAINHAND);
            var atkSpd = modifiers.compute(Attributes.ATTACK_SPEED, 4.0, EquipmentSlot.MAINHAND);
            
            var dps = atkDmg * atkSpd;
            if (bestDPS < dps) {
                bestDPS = dps;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}
