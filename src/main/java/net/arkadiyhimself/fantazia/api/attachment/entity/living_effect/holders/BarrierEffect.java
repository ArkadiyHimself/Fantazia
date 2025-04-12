package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class BarrierEffect extends LivingEffectHolder implements IDamageEventListener {
    private float health = 0;
    private float INITIAL = 0;
    private float color = 0;
    private int hitCD = 0;

    public BarrierEffect(LivingEntity livingEntity) {
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
    public CompoundTag syncSerialize() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        deserializeNBT(getEntity().registryAccess(), tag);
    }

    @Override
    public void tick() {
        super.tick();
        if (hitCD > 0) hitCD--;
        color = Math.max(0, color - 0.2f);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        this.INITIAL = Math.max(1, instance.getAmplifier());
        this.health = Math.max(1, instance.getAmplifier());
    }
    @Override
    public void ended() {
        super.ended();
        remove();
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
                int num = switch (Minecraft.getInstance().options.particles().get()) {
                    case ALL -> 30;
                    case DECREASED -> 25;
                    case MINIMAL -> 20;
                };

                for (int i = 1; i <= num; i++) VisualHelper.randomParticleOnModel(getEntity(), furious ? FTZParticleTypes.PIECES_FURY.random() : FTZParticleTypes.PIECES.random(), VisualHelper.ParticleMovement.FALL);
            }
        } else {
            event.setAmount(-newHP);
            remove();
            getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EFFECT_BARRIER_BREAK.get(), SoundSource.AMBIENT);
            if (event.getEntity().hasEffect(FTZMobEffects.BARRIER)) EffectCleansing.forceCleanse(event.getEntity(), FTZMobEffects.BARRIER);

            for (int i = 1; i <= 20 + 5 * Minecraft.getInstance().options.particles().get().getId(); i++) VisualHelper.randomParticleOnModel(getEntity(), furious ? FTZParticleTypes.PIECES_FURY.random() : FTZParticleTypes.PIECES.random(), VisualHelper.ParticleMovement.FALL);
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
