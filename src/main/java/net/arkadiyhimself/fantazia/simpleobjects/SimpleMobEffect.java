package net.arkadiyhimself.fantazia.simpleobjects;

import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @SuppressWarnings("ConstantConditions")
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        if (this == FTZMobEffects.FROZEN.get()) freezeTick(pLivingEntity);

    }
    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, @NotNull LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
    }

    public void freezeTick(@NotNull LivingEntity pLivingEntity) {
        FTZDamageTypes.DamageSources sources = LevelCapHelper.getDamageSources(pLivingEntity.level());
        if (sources == null) return;
        if (pLivingEntity.fireImmune()) pLivingEntity.hurt(sources.frozen(), 2.25f);
        else if (pLivingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) pLivingEntity.hurt(sources.frozen(), 1f);
    }
}