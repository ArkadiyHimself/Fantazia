package net.arkadiyhimself.fantazia.mobeffects;

import net.arkadiyhimself.fantazia.registry.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SimpleMobEffect extends MobEffect {
    private final boolean isTicking;

    public SimpleMobEffect(MobEffectCategory pCategory, int pColor, boolean isTicking) {
        super(pCategory, pColor);
        this.isTicking = isTicking;
    }
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return isTicking;
    }
    @Override
    public boolean isInstantenous() {
        return !isTicking;
    }
    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        if (this == MobEffectRegistry.FROZEN.get()) freezeTick(pLivingEntity);

    }
    public void freezeTick(@NotNull LivingEntity pLivingEntity) {
        DamageSource FROZEN = new DamageSource(pLivingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.FROZEN));
        if (pLivingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) { pLivingEntity.hurt(FROZEN, 1f); }
        else if (pLivingEntity.fireImmune()) { pLivingEntity.hurt(FROZEN, 2.25f); }
    }
}
