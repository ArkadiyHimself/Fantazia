package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
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
    ResourceKey<DamageType> SIMPLE_CHASING_PROJECTILE = register("simple_chasing_projectile"); // implemented
    ResourceKey<DamageType> BIFROST = register("bifrost"); // implemented
    ResourceKey<DamageType> PIMPILLO_EXPLOSION = register("pimpillo_explosion");
    ResourceKey<DamageType> THROWN_PIN = register("thrown_pin"); // implemented
    ResourceKey<DamageType> BLOCK_FLY = register("block_fly"); // implemented
    ResourceKey<DamageType> OMINOUS_BELL = register("ominous_bell");

    static void bootStrap(BootstrapContext<DamageType> context) {
        context.register(ANCIENT_BURNING, new DamageType("ancient_burning", DamageScaling.NEVER,0.1f, DamageEffects.BURNING));
        context.register(ANCIENT_FLAME, new DamageType("ancient_flame", DamageScaling.NEVER,0.1f, DamageEffects.BURNING));
        context.register(BLEEDING, new DamageType("bleeding", DamageScaling.NEVER,0.1f));
        context.register(ELECTRIC, new DamageType("electric", DamageScaling.NEVER,0.25f, DamageEffects.BURNING));
        context.register(FROZEN, new DamageType("frozen", DamageScaling.NEVER,0.25f, DamageEffects.FREEZING));
        context.register(HATCHET, new DamageType("hatchet", DamageScaling.NEVER,0.1f));
        context.register(PARRY, new DamageType("parry", DamageScaling.NEVER,0.1f));
        context.register(REMOVAL, new DamageType("removal", DamageScaling.NEVER,0f));
        context.register(SHOCKWAVE, new DamageType("shockwave", DamageScaling.NEVER,0.25f));
        context.register(SIMPLE_CHASING_PROJECTILE, new DamageType("simple_chasing_projectile", DamageScaling.NEVER, 0.3f));
        context.register(BIFROST, new DamageType("bifrost", DamageScaling.NEVER,1f));
        context.register(PIMPILLO_EXPLOSION, new DamageType("pimpillo_explosion", DamageScaling.NEVER, 0.1f));
        context.register(THROWN_PIN, new DamageType("thrown_pin", DamageScaling.NEVER,0.25f));
        context.register(BLOCK_FLY, new DamageType("block_fly", DamageScaling.NEVER,0f));
        context.register(OMINOUS_BELL, new DamageType("ominous_bell", DamageScaling.NEVER,0f));
    }

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Fantazia.location(name));
    }
}
