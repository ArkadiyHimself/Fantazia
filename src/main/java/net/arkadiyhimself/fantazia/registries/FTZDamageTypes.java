package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public interface FTZDamageTypes {

    ResourceKey<DamageType> ANCIENT_BURNING = register("ancient_burning"); // implemented
    ResourceKey<DamageType> ANCIENT_FLAME = register("ancient_flame"); // implemented
    ResourceKey<DamageType> BLEEDING = register("bleeding"); // implemented
    ResourceKey<DamageType> ELECTRIC = register("electric"); // implemented
    ResourceKey<DamageType> FROZEN = register("frozen"); // implemented
    ResourceKey<DamageType> HATCHET = register("hatchet"); // implemented
    ResourceKey<DamageType> PARRY = register("parry"); // implemented
    ResourceKey<DamageType> REMOVAL = register("removal"); // implemented
    ResourceKey<DamageType> SHOCKWAVE = register("shockwave"); // implemented
    ResourceKey<DamageType> SIMPLE_CHASING_PROJECTILE = register("simple_chasing_projectile");

    static void bootStrap(BootstrapContext<DamageType> context) {
        context.register(ANCIENT_BURNING, new DamageType("ancient_burning", DamageScaling.NEVER,0.1f));
        context.register(ANCIENT_FLAME, new DamageType("ancient_flame", DamageScaling.NEVER,0.1f));
        context.register(BLEEDING, new DamageType("bleeding", DamageScaling.NEVER,0.1f));
        context.register(ELECTRIC, new DamageType("electric", DamageScaling.NEVER,0.25f));
        context.register(FROZEN, new DamageType("frozen", DamageScaling.NEVER,0.25f));
        context.register(HATCHET, new DamageType("hatchet", DamageScaling.NEVER,0.1f));
        context.register(PARRY, new DamageType("parry", DamageScaling.NEVER,0.1f));
        context.register(REMOVAL, new DamageType("removal", DamageScaling.NEVER,0f));
        context.register(SHOCKWAVE, new DamageType("shockwave", DamageScaling.NEVER,0.25f));
        context.register(SIMPLE_CHASING_PROJECTILE, new DamageType("simple_chasing_projectile", DamageScaling.NEVER, 0.3f));
    }

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Fantazia.res(name));
    }
}
