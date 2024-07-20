package net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class AbsoluteBarrierEffect extends EffectHolder {
    private boolean hasBarrier = false;
    public AbsoluteBarrierEffect(LivingEntity owner) {
        super(owner, MobEffectRegistry.ABSOLUTE_BARRIER.get());
    }
    public boolean hasBarrier() {
        return hasBarrier;
    }
    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        hasBarrier = true;
    }
    @Override
    public void ended() {
        super.ended();
        hasBarrier = false;
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putBoolean(ID + "hasBarrier", hasBarrier);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        hasBarrier = tag.contains(ID + "hasBarrier") && tag.getBoolean(ID +"hasBarrier");
    }
    @Override
    public void respawn() {
        super.respawn();
        hasBarrier = false;
    }
}
