package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.ComplexLivingEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class LayeredBarrierEffectHolder extends ComplexLivingEffectHolder implements IDamageEventListener, ISyncEveryTick {

    private int layers;
    private float color;

    public LayeredBarrierEffectHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("layered_barrier_effect"), FTZMobEffects.LAYERED_BARRIER);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("layers", this.layers);
        tag.putFloat("color", this.color);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        layers = compoundTag.getInt("layers");
        color = compoundTag.getFloat("color");
    }

    @Override
    public CompoundTag serializeTick() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        deserializeNBT(getEntity().registryAccess(), tag);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        color = Math.max(0, color - 0.2f);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (instance.getEffect().value() == FTZMobEffects.LAYERED_BARRIER.value()) layers = instance.getAmplifier() + 1;
    }

    @Override
    public void ended(MobEffect mobEffect) {
        super.ended(mobEffect);
        if (mobEffect == FTZMobEffects.LAYERED_BARRIER.value()) layers = 0;
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        if (!hasBarrier() || event.getSource().is(FTZDamageTypeTags.PIERCES_BARRIER) || getEntity().hasEffect(FTZMobEffects.ABSOLUTE_BARRIER)) return;

        color = 1f;
        event.setCanceled(true);
        layers--;
        if (layers <= 0) {
            EffectCleansing.forceCleanse(getEntity(), FTZMobEffects.LAYERED_BARRIER);
            getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EFFECT_LAYERED_BARRIER_BREAK.get(), SoundSource.AMBIENT);
        } else getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EFFECT_LAYERED_BARRIER_DAMAGE.get(), SoundSource.AMBIENT);;
    }

    public boolean hasBarrier() {
        return layers > 0 && duration() > 0;
    }

    public void remove() {
        layers = 0;
    }

    public int getLayers() {
        return layers;
    }

    public float getColor() {
        return color;
    }
}
