package net.arkadiyhimself.fantazia.common.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.registries.FTZParticleTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public interface HealingTypes {

    ResourceKey<HealingType> DEVOUR = create("devour");
    ResourceKey<HealingType> GENERIC = create("generic");
    ResourceKey<HealingType> LIFESTEAL = create("lifesteal");
    ResourceKey<HealingType> MOB_EFFECT = create("mob_effect");
    ResourceKey<HealingType> MOB_EFFECT_REGEN = create("mob_effect_regen");
    ResourceKey<HealingType> NATURAL_REGEN = create("natural_regen");
    ResourceKey<HealingType> REGEN_AURA = create("regen_aura");
    ResourceKey<HealingType> COMB = create("comb");

    private static ResourceKey<HealingType> create(String string) {
        return ResourceKey.create(FantazicRegistries.Keys.HEALING_TYPE, Fantazia.location(string));
    }

    static void bootStrap(BootstrapContext<HealingType> context) {
        context.register(DEVOUR, new HealingType("devour"));
        context.register(GENERIC, new HealingType("generic"));
        context.register(LIFESTEAL, new HealingType("lifesteal",0f, FTZParticleTypes.LIFESTEAL1.getId(), FTZParticleTypes.LIFESTEAL2.getId(), FTZParticleTypes.LIFESTEAL3.getId(), FTZParticleTypes.LIFESTEAL4.getId(), FTZParticleTypes.LIFESTEAL5.getId()));
        context.register(MOB_EFFECT, new HealingType("mobEffect"));
        context.register(MOB_EFFECT_REGEN, new HealingType("mobEffectRegen"));
        context.register(NATURAL_REGEN, new HealingType("naturalRegen",0.005f));
        context.register(REGEN_AURA, new HealingType("regenAura",0f, FTZParticleTypes.REGEN1.getId(), FTZParticleTypes.REGEN2.getId(), FTZParticleTypes.REGEN3.getId()));
        context.register(COMB, new HealingType("comb"));
    }
}
