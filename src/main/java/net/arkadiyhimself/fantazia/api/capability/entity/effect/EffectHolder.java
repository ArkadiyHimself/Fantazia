package net.arkadiyhimself.fantazia.api.capability.entity.effect;

import net.arkadiyhimself.fantazia.api.capability.IEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class EffectHolder implements IEffect {
    private static final String durationId = "duration";
    private static final String initialId = "initial";
    private final LivingEntity owner;
    private final MobEffect mobEffect;
    protected int initialDur = 1;
    protected int duration = 0;
    protected EffectHolder(LivingEntity owner, MobEffect mobEffect) {
        this.owner = owner;
        this.mobEffect = mobEffect;
    }
    @Override
    public int getInitDur() {
        return initialDur;
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
        initialDur = 0;
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
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        if (unSyncedDuration()) return tag;
        tag.putInt(durationId, duration);
        tag.putInt(initialId, initialDur);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        if (unSyncedDuration()) return;
        duration = tag.contains(durationId) ? tag.getInt(durationId) : 0;
        if (tag.contains(initialId)) initialDur = tag.getInt(initialId);
    }
    @Override
    public void tick() {
        MobEffectInstance effectInstance = getOwner().getEffect(getEffect());
        if (effectInstance == null) duration = 0;
        else duration = effectInstance.isInfiniteDuration() ? getInitDur() : effectInstance.getDuration();
    }
    public String ID() {
        ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
        return id == null ? null : id.toString();
    }
    public boolean unSyncedDuration() {
        return false;
    }
}
