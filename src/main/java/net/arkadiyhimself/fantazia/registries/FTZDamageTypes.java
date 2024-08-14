package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class FTZDamageTypes {
    public static final ResourceKey<DamageType> REMOVAL = register("removal"); // implemented
    public static final ResourceKey<DamageType> BLEEDING = register("bleeding"); // implemented
    public static final ResourceKey<DamageType> FROZEN = register("frozen"); // implemented
    public static final ResourceKey<DamageType> PARRY = register("parry"); // implemented
    public static final ResourceKey<DamageType> HATCHET = register("hatchet"); // implemented
    public static final ResourceKey<DamageType> ANCIENT_FLAME = register("ancient_flame"); // implemented
    public static final ResourceKey<DamageType> ANCIENT_BURNING = register("ancient_burning"); // implemented
    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Fantazia.res(name));
    }
}
