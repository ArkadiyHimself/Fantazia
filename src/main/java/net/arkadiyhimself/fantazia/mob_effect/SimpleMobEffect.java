package net.arkadiyhimself.fantazia.mob_effect;

import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class SimpleMobEffect extends MobEffect {

    private final boolean isTicking;
    private final boolean patchouliEntry;

    private final BiConsumer<LivingEntity, Integer> applyTicking;

    public SimpleMobEffect(MobEffectCategory pCategory, int pColor, boolean isTicking, boolean patchouliEntry) {
        super(pCategory, pColor);
        this.isTicking = isTicking;
        this.applyTicking = (livingEntity, integer) -> {};
        this.patchouliEntry = patchouliEntry;
    }

    public SimpleMobEffect(MobEffectCategory pCategory, int pColor, BiConsumer<LivingEntity, Integer> applyTicking, boolean patchouliEntry) {
        super(pCategory, pColor);
        this.isTicking = true;
        this.applyTicking = applyTicking;
        this.patchouliEntry = patchouliEntry;
    }

    public SimpleMobEffect(MobEffectCategory pCategory, int pColor, BiConsumer<LivingEntity, Integer> applyTicking) {
        this(pCategory, pColor, applyTicking, true);
    }

    public SimpleMobEffect(MobEffectCategory pCategory, int pColor, boolean isTicking) {
        this(pCategory, pColor, isTicking, true);
    }

    @Override
    public boolean isInstantenous() {
        return !isTicking;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        this.applyTicking.accept(pLivingEntity, pAmplifier);
        if (this == FTZMobEffects.FROZEN.value()) freezeTick(pLivingEntity);
        return this.isTicking;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return isTicking;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, @NotNull LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
    }

    @Override
    public @NotNull SimpleMobEffect addAttributeModifier(@NotNull Holder<Attribute> attribute, @NotNull ResourceLocation id, double amount, AttributeModifier.@NotNull Operation operation) {
        super.addAttributeModifier(attribute, id, amount, operation);
        return this;
    }

    private void freezeTick(@NotNull LivingEntity pLivingEntity) {
        DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(pLivingEntity.level());
        if (sources == null) return;
        if (pLivingEntity.fireImmune()) pLivingEntity.hurt(sources.frozen(), 2.25f);
        else if (pLivingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) pLivingEntity.hurt(sources.frozen(), 1f);
    }

    public boolean patchouliEntry() {
        return patchouliEntry;
    }
}
