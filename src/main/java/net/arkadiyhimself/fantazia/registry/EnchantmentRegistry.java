package net.arkadiyhimself.fantazia.registry;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.enchantments.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantmentRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Fantazia.MODID);
    public static void register(IEventBus eventBus) {
        ENCHANTMENT.register(eventBus);
    }

    // hatchet
    public static final RegistryObject<Enchantment> PHASING;
    public static final RegistryObject<Enchantment> RICOCHET;
    public static final RegistryObject<Enchantment> HEADSHOT;

    // for vanilla equipment
    public static final RegistryObject<Enchantment> DISINTEGRATION; // finished
    public static final RegistryObject<Enchantment> ICE_ASPECT; // finished
    public static final RegistryObject<Enchantment> DECISIVE_STRIKE; // finished

    static {
        PHASING = ENCHANTMENT.register("phasing", Phasing::new);
        RICOCHET = ENCHANTMENT.register("ricochet", Ricochet::new);
        HEADSHOT = ENCHANTMENT.register("headshot", Headshot::new);

        DISINTEGRATION = ENCHANTMENT.register("disintegration", Disintegration::new);
        ICE_ASPECT = ENCHANTMENT.register("ice_aspect", IceAspect::new);
        DECISIVE_STRIKE = ENCHANTMENT.register("decisive_strike", DecisiveStrike::new);
    }
}
