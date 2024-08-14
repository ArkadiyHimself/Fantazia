package net.arkadiyhimself.fantazia.advanced.healing;

import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.registries.custom.FTZHealingTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class HealingSources {
    private final Registry<HealingType> healingTypes;
    private final HealingSource generic;
    private final HealingSource naturalRegen;
    private final HealingSource mobEffectRegen;
    private final HealingSource mobEffect;
    public HealingSources(RegistryAccess access) {
        healingTypes = access.registryOrThrow(FantazicRegistry.Keys.HEALING_TYPE);

        this.generic = this.source(FTZHealingTypes.GENERIC);
        this.naturalRegen = this.source(FTZHealingTypes.NATURAL_REGEN);
        this.mobEffectRegen = this.source(FTZHealingTypes.MOB_EFFECT_REGEN);
        this.mobEffect = this.source(FTZHealingTypes.MOB_EFFECT);
    }
    private HealingSource source(ResourceKey<HealingType> healingTypeResourceKey) {
        return new HealingSource(this.healingTypes.getHolderOrThrow(healingTypeResourceKey));
    }
    private HealingSource source(ResourceKey<HealingType> healingTypeResourceKey, @Nullable Entity entity) {
        return new HealingSource(this.healingTypes.getHolderOrThrow(healingTypeResourceKey), entity);
    }
    public HealingSource generic() {
        return generic;
    }
    public HealingSource naturalRegen() {
        return this.naturalRegen;
    }
    public HealingSource mobEffectRegen() {
        return mobEffectRegen;
    }
    public HealingSource mobEffect() {
        return mobEffect;
    }

    // heal from entities
    public HealingSource lifesteal(@Nullable Entity entity) {
        return this.source(FTZHealingTypes.LIFESTEAL, entity);
    }
    public HealingSource regenAura(@Nullable Entity entity) {
        return this.source(FTZHealingTypes.REGEN_AURA, entity);
    }
    public HealingSource devour(@Nullable Entity entity) {
        return this.source(FTZHealingTypes.DEVOUR, entity);
    }
}
