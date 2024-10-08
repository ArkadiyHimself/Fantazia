package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface FTZDamageTypes {
    ResourceKey<DamageType> REMOVAL = register("removal"); // implemented
    ResourceKey<DamageType> BLEEDING = register("bleeding"); // implemented
    ResourceKey<DamageType> FROZEN = register("frozen"); // implemented
    ResourceKey<DamageType> ANCIENT_FLAME = register("ancient_flame"); // implemented
    ResourceKey<DamageType> ANCIENT_BURNING = register("ancient_burning"); // implemented
    ResourceKey<DamageType> PARRY = register("parry"); // implemented
    ResourceKey<DamageType> HATCHET = register("hatchet"); // implemented
    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Fantazia.res(name));
    }}
