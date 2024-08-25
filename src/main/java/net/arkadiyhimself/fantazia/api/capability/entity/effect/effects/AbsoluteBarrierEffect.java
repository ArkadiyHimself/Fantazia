package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AbsoluteBarrierEffect extends EffectHolder implements IDamageReacting {
    private boolean hasBarrier = false;
    public AbsoluteBarrierEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.ABSOLUTE_BARRIER.get());
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
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = super.serialize(toDisk);
        tag.putBoolean("hasBarrier", hasBarrier);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        super.deserialize(tag, fromDisk);
        hasBarrier = tag.contains("hasBarrier") && tag.getBoolean("hasBarrier");
    }
    @Override
    public void respawn() {
        super.respawn();
        hasBarrier = false;
    }
    @Override
    public void onHit(LivingAttackEvent event) {
        if (getDur() > 0 && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) event.setCanceled(true);
    }
    @Override
    public boolean syncedDuration() {
        return false;
    }
}
