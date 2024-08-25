package net.arkadiyhimself.fantazia.api.capability.entity.effect;

import net.arkadiyhimself.fantazia.api.capability.IEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class EffectHolder implements IEffect {
    private final LivingEntity owner;
    private final MobEffect mobEffect;
    protected int INITIAL_DUR = 1;
    protected int duration = 0;
    public EffectHolder(LivingEntity owner, MobEffect mobEffect) {
        this.owner = owner;
        this.mobEffect = mobEffect;
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
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        if (!syncedDuration()) return tag;
        tag.putInt("duration", duration);
        tag.putInt("initial_dur", INITIAL_DUR);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        if (!syncedDuration()) return;
        duration = tag.contains("duration") ? tag.getInt("duration") : 0;
        if (tag.contains("initial_dur")) INITIAL_DUR = tag.getInt("initial_dur");
    }
    @Override
    public void tick() {
        MobEffectInstance effectInstance = getOwner().getEffect(getEffect());
        if (effectInstance == null) duration = 0;
        else duration = effectInstance.getDuration();
    }
    public String ID() {
        ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
        return id == null ? null : id.toString();
    }
    public boolean syncedDuration() {
        return true;
    }
}
