package net.arkadiyhimself.fantazia.enchantments;

import net.arkadiyhimself.fantazia.Items.weapons.Range.HatchetItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Phasing extends Enchantment {
    public Phasing() {
        super(Rarity.COMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem() instanceof HatchetItem;
    }

    @Override
    public int getMinCost(int pLevel) {
        return 5 + pLevel * 7;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && !(pOther instanceof Ricochet);
    }
}
