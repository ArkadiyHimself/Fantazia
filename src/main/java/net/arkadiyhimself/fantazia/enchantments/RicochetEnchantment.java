package net.arkadiyhimself.fantazia.enchantments;

import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class RicochetEnchantment extends Enchantment {
    public RicochetEnchantment() {
        super(Rarity.COMMON, FTZEnchantments.Categories.HATCHET, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
    protected boolean checkCompatibility(@NotNull Enchantment pOther) {
        return super.checkCompatibility(pOther) && !(pOther instanceof PhasingEnchantment);
    }
    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return stack.getItem() instanceof HatchetItem;
    }
}
