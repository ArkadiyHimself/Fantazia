package net.arkadiyhimself.fantazia.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.FireAspectEnchantment;
import org.jetbrains.annotations.NotNull;

public class IceAspectEnchantment extends Enchantment {
    public IceAspectEnchantment() {
        super(Enchantment.Rarity.COMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinCost(int pEnchantmentLevel) {
        return 10 + 20 * (pEnchantmentLevel - 1);
    }

    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment pOther) {
        return !(pOther instanceof FireAspectEnchantment) && super.checkCompatibility(pOther);
    }
}
