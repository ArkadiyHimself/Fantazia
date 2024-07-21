package net.arkadiyhimself.fantazia.enchantments;

import net.arkadiyhimself.fantazia.Items.weapons.Range.HatchetItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Headshot extends Enchantment {
    public Headshot() {
        super(Rarity.COMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem() instanceof HatchetItem;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
    @Override
    public int getMinCost(int pEnchantmentLevel) {
        return 1 + (pEnchantmentLevel - 1) * 8;
    }
    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return this.getMinCost(pEnchantmentLevel) + 20;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && !(pOther instanceof DamageEnchantment);
    }
}
