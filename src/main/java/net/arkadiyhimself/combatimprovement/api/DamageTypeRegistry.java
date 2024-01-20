package net.arkadiyhimself.combatimprovement.api;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> BLEEDING = register("bleeding");
    public static final ResourceKey<DamageType> FROZEN = register("frozen");
    public static final ResourceKey<DamageType> PARRY = register("parry");
    public static final ResourceKey<DamageType> HATCHET = register("hatchet");
    public static final ResourceKey<DamageType> ANCIENT_FLAME = register("ancient_flame");
    private static ResourceKey<DamageType> register(String name)
    {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CombatImprovement.MODID, name));
    }
    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(BLEEDING, new DamageType("bleeding", 0.1F));
        context.register(FROZEN, new DamageType("frozen", 0.1F));
        context.register(PARRY, new DamageType("parry", 0.1F));
        context.register(HATCHET, new DamageType("hatchet", 0.1F));
        context.register(ANCIENT_FLAME, new DamageType("ancient_flame", 0.1F));
    }
}
