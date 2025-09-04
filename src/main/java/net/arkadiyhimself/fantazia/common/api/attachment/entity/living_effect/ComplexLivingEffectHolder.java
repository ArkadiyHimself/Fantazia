package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public abstract class ComplexLivingEffectHolder implements IComplexLivingEffectHolder {

    private final LivingEntity livingEntity;
    private final ResourceLocation id;
    private final Holder<MobEffect> mobEffect;
    protected int initialDur = 1;
    protected int duration = 0;

    protected ComplexLivingEffectHolder(LivingEntity livingEntity, ResourceLocation id, @NotNull Holder<MobEffect> mobEffect) {
        this.livingEntity = livingEntity;
        this.id = id;
        this.mobEffect = mobEffect;
    }

    @Override
    public final @NotNull LivingEntity getEntity() {
        return this.livingEntity;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("duration", duration);
        tag.putInt("initial", initialDur);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        duration = compoundTag.getInt("duration");
        if (compoundTag.contains("initial")) initialDur = compoundTag.getInt("initial");
    }

    @Override
    public CompoundTag serializeInitial() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        deserializeNBT(getEntity().registryAccess(), tag);
    }

    @Override
    public final ResourceLocation id() {
        return this.id;
    }

    @Override
    public final int initialDuration() {
        return initialDur;
    }

    @Override
    public final int duration() {
        return duration;
    }

    @Override
    public final @NotNull Holder<MobEffect> getEffect() {
        return mobEffect;
    }

    @Override
    public void added(MobEffectInstance instance) {
        if (getEffect().value() != instance.getEffect().value()) return;
        if (instance.isInfiniteDuration()) {
            this.initialDur = 1;
            this.duration = 1;
        } else {
            this.initialDur = instance.getDuration();
            this.duration = instance.getDuration();
        }
    }

    @Override
    public void ended(MobEffect effect) {
        if (getEffect().value() == effect) this.duration = 0;
    }

    @Override
    public void serverTick() {
        MobEffectInstance effectInstance = getEntity().getEffect(getEffect());
        if (effectInstance == null) duration = 0;
        else duration = effectInstance.isInfiniteDuration() ? initialDuration() : effectInstance.getDuration();
    }

}
