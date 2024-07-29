package net.arkadiyhimself.fantazia.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class BullyEnchantment extends Enchantment {
    public BullyEnchantment() {
        super(Rarity.COMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinCost(int pLevel) {
        return 5 + (pLevel - 1) * 8;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
    @Override
    protected boolean checkCompatibility(@NotNull Enchantment pOther) {
        return !(pOther instanceof DamageEnchantment) && super.checkCompatibility(pOther);
    }
}
