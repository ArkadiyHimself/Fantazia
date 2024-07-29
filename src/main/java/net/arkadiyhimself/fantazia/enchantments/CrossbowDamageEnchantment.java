package net.arkadiyhimself.fantazia.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class CrossbowDamageEnchantment extends Enchantment {
    public CrossbowDamageEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.CROSSBOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinCost(int pLevel) {
        return 1 + (pLevel - 1) * 10;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment pOther) {
        return super.checkCompatibility(pOther) && !(pOther instanceof CrossbowDamageEnchantment);
    }
}
