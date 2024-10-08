package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import net.arkadiyhimself.fantazia.api.type.entity.ILivingEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public abstract class LivingEffectHolder implements ILivingEffect {
    private final LivingEntity livingEntity;
    private final ResourceLocation id;
    private final Holder<MobEffect> mobEffect;
    protected int initialDur = 1;
    protected int duration = 0;
    protected LivingEffectHolder(LivingEntity livingEntity, ResourceLocation id, @NotNull Holder<MobEffect> mobEffect) {
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
    public CompoundTag syncSerialize() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
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
        if (instance.isInfiniteDuration()) {
            this.initialDur = 1;
            this.duration = 1;
        } else {
            this.initialDur = instance.getDuration();
            this.duration = instance.getDuration();
        }
    }

    @Override
    public void ended() {
        this.duration = 0;
    }

    @Override
    public void tick() {
        MobEffectInstance effectInstance = getEntity().getEffect(getEffect());
        if (effectInstance == null) duration = 0;
        else duration = effectInstance.isInfiniteDuration() ? initialDuration() : effectInstance.getDuration();
    }
}
