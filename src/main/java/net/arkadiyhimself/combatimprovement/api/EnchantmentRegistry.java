package net.arkadiyhimself.combatimprovement.api;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Enchantments.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantmentRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CombatImprovement.MODID);
    public static void register(IEventBus eventBus) {
        ENCHANTMENT.register(eventBus);
    }

    // hatchet
    public static final RegistryObject<Enchantment> PHASING;
    public static final RegistryObject<Enchantment> RICOCHET;

    // for vanilla equipment
    public static final RegistryObject<Enchantment> DISINTEGRATION;
    public static final RegistryObject<Enchantment> ICE_ASPECT;
    public static final RegistryObject<Enchantment> DECISIVE_STRIKE;

    static {
        PHASING = ENCHANTMENT.register("phasing", Phasing::new);
        RICOCHET = ENCHANTMENT.register("ricochet", Ricochet::new);

        DISINTEGRATION = ENCHANTMENT.register("disintegration", Disintegration::new);
        ICE_ASPECT = ENCHANTMENT.register("ice_aspect", IceAspect::new);
        DECISIVE_STRIKE = ENCHANTMENT.register("decisive_strike", DecisiveStrike::new);
    }
}
