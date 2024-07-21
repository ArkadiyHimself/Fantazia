package net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectHolder;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.arkadiyhimself.fantazia.util.interfaces.IDamageReacting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AbsoluteBarrierEffect extends EffectHolder implements IDamageReacting {
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
    @Override
    public void onHit(LivingAttackEvent event) {
        if (getDur() > 0) event.setCanceled(true);
    }
}
