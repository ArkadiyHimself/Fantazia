package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.ComplexLivingEffectHolder;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
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

public class BarrierEffectHolder extends ComplexLivingEffectHolder implements IDamageEventListener, ISyncEveryTick {
    private float health = 0;
    private float INITIAL = 0;
    private float color = 0;
    private int hitCD = 0;

    public BarrierEffectHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("barrier_effect"), FTZMobEffects.BARRIER);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putFloat("health", health);
        tag.putFloat("initial", INITIAL);
        tag.putFloat("color", color);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        health = compoundTag.contains("health") ? compoundTag.getFloat("health") : 0;
        INITIAL = compoundTag.contains("initial") ? compoundTag.getFloat("initial") : 0;
        color = compoundTag.contains("color") ? compoundTag.getFloat("color") : 0;
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
        if (hitCD > 0) hitCD--;
        color = Math.max(0, color - 0.2f);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (instance.getEffect().value() != FTZMobEffects.BARRIER.value()) return;
        this.INITIAL = Math.max(1, instance.getAmplifier());
        this.health = Math.max(1, instance.getAmplifier());
    }

    @Override
    public void ended(MobEffect effect) {
        super.ended(effect);
        if (effect == FTZMobEffects.BARRIER.value()) remove();
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        if (!hasBarrier() || event.getSource().is(FTZDamageTypeTags.PIERCES_BARRIER) || event.isCanceled() || getEntity().hasEffect(FTZMobEffects.ABSOLUTE_BARRIER)) return;

        color = 1f;
        float dmg = event.getAmount();
        float newHP = health - dmg;
        boolean furious = getEntity().hasEffect(FTZMobEffects.FURY);
        if (newHP > 0) {
            event.setCanceled(true);
            health -= dmg;

            if (hitCD <= 0) {
                getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EFFECT_BARRIER_DAMAGE.get(), SoundSource.AMBIENT);
                hitCD = 10;
                int num = (int) Math.min(dmg * 3f, 25);

                VisualHelper.particleOnEntityServer(getEntity(), furious ? FTZParticleTypes.PIECES_FURY.random() : FTZParticleTypes.PIECES.random(), ParticleMovement.FALL, num);
            }
        } else {
            event.setAmount(-newHP);
            remove();
            getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EFFECT_BARRIER_BREAK.get(), SoundSource.AMBIENT);
            if (event.getEntity().hasEffect(FTZMobEffects.BARRIER)) EffectCleansing.forceCleanse(event.getEntity(), FTZMobEffects.BARRIER);

            VisualHelper.particleOnEntityServer(getEntity(), furious ? FTZParticleTypes.PIECES_FURY.random() : FTZParticleTypes.PIECES.random(), ParticleMovement.FALL, 30);
        }
    }

    public float getHealth() {
        return health;
    }

    public float getInitial() {
        return INITIAL;
    }

    public boolean hasBarrier() {
        return health > 0 && duration() > 0;
    }

    public void remove() {
        health = 0;
    }

    public float getColor() {
        return color;
    }


}
