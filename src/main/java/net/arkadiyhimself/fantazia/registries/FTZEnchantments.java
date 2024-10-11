package net.arkadiyhimself.fantazia.registries;


import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public interface FTZEnchantments {

    ResourceKey<Enchantment> DISINTEGRATION = key("disintegration");
    ResourceKey<Enchantment> ICE_ASPECT = key("ice_aspect");
    ResourceKey<Enchantment> FREEZE = key("freeze");
    ResourceKey<Enchantment> DECISIVE_STRIKE = key("decisive_strike");
    ResourceKey<Enchantment> BULLY = key("bully");
    // crossbows
    ResourceKey<Enchantment> DUELIST = key("duelist");
    ResourceKey<Enchantment> BALLISTA = key("ballista");
    // hatchets
    ResourceKey<Enchantment> PHASING = key("phasing");
    ResourceKey<Enchantment> RICOCHET = key("ricochet");
    ResourceKey<Enchantment> HEADSHOT = key("headshot");

    private static ResourceKey<Enchantment> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Fantazia.MODID, pName));
    }
}
