package net.arkadiyhimself.fantazia.api;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> BLEEDING = register("bleeding"); // implemented
    public static final ResourceKey<DamageType> FROZEN = register("frozen"); // implemented
    public static final ResourceKey<DamageType> PARRY = register("parry"); // implemented
    public static final ResourceKey<DamageType> HATCHET = register("hatchet"); // implemented
    public static final ResourceKey<DamageType> ANCIENT_FLAME = register("ancient_flame"); // implemented
    public static final ResourceKey<DamageType> ANCIENT_BURNING = register("ancient_burning"); // implemented
    private static ResourceKey<DamageType> register(String name)
    {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Fantazia.res(name));
    }
    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(BLEEDING, new DamageType("bleeding", 0.1F));
        context.register(FROZEN, new DamageType("frozen", 0.1F));
        context.register(PARRY, new DamageType("parry", 0.1F));
        context.register(HATCHET, new DamageType("hatchet", 0.1F));
        context.register(ANCIENT_FLAME, new DamageType("ancient_flame", 0.1F));
        context.register(ANCIENT_BURNING, new DamageType("ancient_burning", 0.25f));
    }
}
