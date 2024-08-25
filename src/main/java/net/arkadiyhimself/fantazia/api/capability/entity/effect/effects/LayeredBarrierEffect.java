package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class LayeredBarrierEffect extends EffectHolder implements IDamageReacting {
    private int layers;
    private float color;
    public LayeredBarrierEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.LAYERED_BARRIER.get());
    }
    public boolean hasBarrier() {
        return layers > 0;
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
    @Override
    public void tick() {
        super.tick();
        color = Math.max(0, color - 0.2f);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        layers = instance.getAmplifier() + 1;
    }

    @Override
    public void ended() {
        super.ended();
        layers = 0;
    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = super.serialize(toDisk);
        tag.putInt("layers", this.layers);
        tag.putFloat("color", this.color);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        super.deserialize(tag, fromDisk);
        layers = tag.contains("layers") ? tag.getInt("layers") : 0;
        color = tag.contains("color") ? tag.getFloat("color") : 0;
    }

    @Override
    public void respawn() {
        super.respawn();
        remove();
    }

    @Override
    public void onHit(LivingHurtEvent event) {
        if (!hasBarrier() || event.getSource().is(FTZDamageTypeTags.PIERCES_BARRIER)) return;

        color = 1f;
        event.setCanceled(true);
        layers--;
        if (layers <= 0 && getOwner().hasEffect(FTZMobEffects.LAYERED_BARRIER.get())) EffectCleansing.forceCleanse(getOwner(), FTZMobEffects.LAYERED_BARRIER.get());
    }

    @Override
    public boolean syncedDuration() {
        return false;
    }
}
