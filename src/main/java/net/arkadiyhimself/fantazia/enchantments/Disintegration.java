package net.arkadiyhimself.fantazia.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;

import java.util.ArrayList;
import java.util.List;

public class Disintegration extends Enchantment {
    public static final List<Item> IGNORED = new ArrayList<>(){{
        add(Items.NETHER_STAR);
    }};
    public Disintegration() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    @Override
    public int getMinCost(int pEnchantmentLevel) {
        return 15 + (pEnchantmentLevel - 1) * 9;
    }
    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 50;
    }
    @Override
    public int getMaxLevel() {
        return 3;
    }
    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return !(pOther instanceof LootBonusEnchantment) && super.checkCompatibility(pOther);
    }
}
