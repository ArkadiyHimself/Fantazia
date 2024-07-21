package net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager;

import net.arkadiyhimself.fantazia.util.interfaces.IEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public abstract class EffectHolder implements IEffect {
    private final LivingEntity owner;
    private final MobEffect mobEffect;
    protected final String ID;
    protected int INITIAL_DUR = 1;
    protected int duration = 0;
    public EffectHolder(LivingEntity owner, MobEffect mobEffect) {
        this.owner = owner;
        this.mobEffect = mobEffect;
        this.ID = mobEffect.getDescriptionId() + ":";
    }
    @Override
    public int getInitDur() {
        return INITIAL_DUR;
    }
    @Override
    public int getDur() {
        return duration;
    }
    @Override
    public MobEffect getEffect() {
        return mobEffect;
    }
    @Override
    public LivingEntity getOwner() {
        return owner;
    }
    @Override
    public void respawn() {
        duration = 0;
        INITIAL_DUR = 0;
    }
    @Override
    public void added(MobEffectInstance instance) {
        this.INITIAL_DUR = instance.getDuration();
        this.duration = instance.getDuration();
    }
    @Override
    public void ended() {
        this.duration = 0;
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(ID + "duration", duration);
        tag.putInt(ID + "initial_dur", INITIAL_DUR);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        duration = tag.contains(ID + "duration") ? tag.getInt(ID + "duration") : 0;
        if (tag.contains(ID + "initial_dur")) INITIAL_DUR = tag.getInt(ID + "initial_dur");
    }
    @Override
    public void tick() {
        MobEffectInstance effectInstance = getOwner().getEffect(this.getEffect());
        if (effectInstance == null) duration = 0;
        else duration = effectInstance.getDuration();
    }
}
