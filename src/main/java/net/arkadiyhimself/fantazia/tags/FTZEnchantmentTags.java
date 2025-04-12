package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public interface FTZEnchantmentTags {

    TagKey<Enchantment> CROSSBOW_DAMAGE_EXCLUSIVE = create("exclusive_set/crossbow_damage");
    TagKey<Enchantment> HATCHET_BEHAVIOUR_EXCLUSIVE = create("exclusive_set/hatchet_behaviour");

    private static TagKey<Enchantment> create(String pName) {
        return TagKey.create(Registries.ENCHANTMENT, Fantazia.res(pName));
    }
}
