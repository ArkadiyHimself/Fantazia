package net.arkadiyhimself.fantazia.enchantments;

import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class HeadshotEnchantment extends Enchantment {
    public HeadshotEnchantment() {
        super(Rarity.COMMON, FTZEnchantments.Categories.HATCHET, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
    protected boolean checkCompatibility(@NotNull Enchantment pOther) {
        return super.checkCompatibility(pOther) && !(pOther instanceof DamageEnchantment);
    }
    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return stack.getItem() instanceof HatchetItem;
    }
}
