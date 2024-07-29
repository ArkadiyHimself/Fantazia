package net.arkadiyhimself.fantazia.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.ArrowFireEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class FreezeEnchantment extends Enchantment {
    public FreezeEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int pLevel) {
        return 20;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return 50;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment pOther) {
        return !(pOther instanceof ArrowFireEnchantment) && super.checkCompatibility(pOther);
    }
}
