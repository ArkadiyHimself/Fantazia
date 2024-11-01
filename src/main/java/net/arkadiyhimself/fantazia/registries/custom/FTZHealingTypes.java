package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.api.FantazicRegistries;
import net.minecraft.resources.ResourceKey;

public interface FTZHealingTypes {
    ResourceKey<HealingType> GENERIC = create("generic");
    ResourceKey<HealingType> NATURAL_REGEN = create("natural_regen");
    ResourceKey<HealingType> MOB_EFFECT_REGEN = create("mob_effect_regen");
    ResourceKey<HealingType> MOB_EFFECT = create("mob_effect");
    ResourceKey<HealingType> LIFESTEAL = create("lifesteal");
    ResourceKey<HealingType> REGEN_AURA = create("regen_aura");
    ResourceKey<HealingType> DEVOUR = create("devour");
    private static ResourceKey<HealingType> create(String string) {
        return ResourceKey.create(FantazicRegistries.Keys.HEALING_TYPE, Fantazia.res(string));
    }
}
