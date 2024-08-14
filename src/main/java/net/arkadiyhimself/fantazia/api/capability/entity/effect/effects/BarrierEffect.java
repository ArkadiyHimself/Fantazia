package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.particless.BarrierParticle;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class BarrierEffect extends EffectHolder implements IDamageReacting {
    private float health = 0;
    private float INITIAL = 0;
    private float color = 0;
    private int hitCD = 0;
    @SuppressWarnings("ConstantConditions")
    public BarrierEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.BARRIER);
    }
    public float getHealth() {
        return health;
    }
    public float getInitial() {
        return INITIAL;
    }
    public boolean hasBarrier() {
        return health > 0 && getDur() > 0;
    }
    public void remove() {
        health = 0;
    }
    public float getColor() {
        return color;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onHit(LivingHurtEvent event) {
        if (!hasBarrier() || event.getSource().is(FTZDamageTypeTags.PIERCES_BARRIER)) return;

        color = 1f;
        float dmg = event.getAmount();
        float newHP = health - dmg;
        boolean furious = getOwner().hasEffect(FTZMobEffects.FURY);
        if (newHP > 0) {
            event.setCanceled(true);
            health -= dmg;

            if (hitCD <= 0) {
                getOwner().level().playSound(null, getOwner().blockPosition(), FTZSoundEvents.BARRIER_HIT, SoundSource.AMBIENT);
                hitCD = 10;
                int num = switch (Minecraft.getInstance().options.particles().get()) {
                    case ALL -> 30;
                    case DECREASED -> 25;
                    case MINIMAL -> 20;
                };

                for (int i = 1; i <= num; i++) VisualHelper.randomParticleOnModel(getOwner(), furious ? BarrierParticle.PIECES_FURY.random() : BarrierParticle.PIECES.random(), VisualHelper.ParticleMovement.FALL);
            }
        } else {
            event.setAmount(-newHP);
            remove();
            getOwner().level().playSound(null, getOwner().blockPosition(), FTZSoundEvents.BARRIER_BREAK, SoundSource.AMBIENT);
            if (event.getEntity().hasEffect(FTZMobEffects.BARRIER)) EffectCleansing.forceCleanse(event.getEntity(), FTZMobEffects.BARRIER);

            int num = switch (Minecraft.getInstance().options.particles().get()) {
                case ALL -> 30;
                case DECREASED -> 25;
                case MINIMAL -> 20;
            };
            for (int i = 1; i <= num; i++) VisualHelper.randomParticleOnModel(getOwner(), furious ? BarrierParticle.PIECES_FURY.random() : BarrierParticle.PIECES.random(), VisualHelper.ParticleMovement.FALL);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (hitCD > 0) hitCD--;
        color = Math.max(0, color - 0.2f);
    }
    @Override
    public void respawn() {
        super.respawn();
        remove();
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
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putFloat(ID + "health", health);
        tag.putFloat(ID + "initial", INITIAL);
        tag.putFloat(ID + "color", color);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        health = tag.contains(ID + "health") ? tag.getFloat(ID + "health") : 0;
        INITIAL = tag.contains(ID + "initial") ? tag.getFloat(ID + "initial") : 0;
        color = tag.contains(ID + "color") ? tag.getFloat(ID + "color") : 0;
    }
}
