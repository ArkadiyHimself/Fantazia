package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.enchantments.*;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FTZEnchantments {
    private static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Fantazia.MODID);
    public static final RegistryObject<Enchantment> DISINTEGRATION = REGISTER.register("disintegration", DisintegrationEnchantment::new);
    public static final RegistryObject<Enchantment> ICE_ASPECT = REGISTER.register("ice_aspect", IceAspectEnchantment::new);
    public static final RegistryObject<Enchantment> FREEZE = REGISTER.register("freeze", FreezeEnchantment::new);
    public static final RegistryObject<Enchantment> DECISIVE_STRIKE = REGISTER.register("decisive_strike", DecisiveStrikeEnchantment::new);
    public static final RegistryObject<Enchantment> BULLY = REGISTER.register("bully", BullyEnchantment::new);
    public static final RegistryObject<Enchantment> DUELIST = REGISTER.register("duelist", CrossbowDamageEnchantment::new);
    public static final RegistryObject<Enchantment> BALLISTA = REGISTER.register("ballista", CrossbowDamageEnchantment::new);

    //  hatchet enchantments
    public static final RegistryObject<Enchantment> PHASING = REGISTER.register("phasing", PhasingEnchantment::new);
    public static final RegistryObject<Enchantment> RICOCHET = REGISTER.register("ricochet", RicochetEnchantment::new);
    public static final RegistryObject<Enchantment> HEADSHOT = REGISTER.register("headshot", HeadshotEnchantment::new);
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    public static class Categories {
        public static final EnchantmentCategory HATCHET = EnchantmentCategory.create("hatchet", item -> item instanceof HatchetItem);
    }
}
