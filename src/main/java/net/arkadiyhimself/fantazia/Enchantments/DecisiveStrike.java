package net.arkadiyhimself.fantazia.Enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;

public class DecisiveStrike extends Enchantment {
    public DecisiveStrike() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int pEnchantmentLevel) { return 5 + (pEnchantmentLevel - 1) * 9; }

    @Override
    public int getMaxCost(int pEnchantmentLevel) { return this.getMinCost(pEnchantmentLevel) + 15; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return !(pOther instanceof SweepingEdgeEnchantment) && super.checkCompatibility(pOther);
    }
}
