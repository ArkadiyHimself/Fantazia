package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.IHealReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class HaemorrhageEffect extends EffectHolder implements IDamageReacting, IHealReacting {
    private float toHeal = 0;
    private int soundCD = 0;
    public HaemorrhageEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.HAEMORRHAGE.get());
    }
    private boolean shouldEmitSound() {
        return soundCD < 0;
    }
    private void emitSound() {
        soundCD = 10;
        getOwner().level().playSound(null, getOwner().blockPosition(), FTZSoundEvents.BLOODLOSS.get(), SoundSource.HOSTILE);
    }
    public void tryMakeSound() {
        if (shouldEmitSound()) emitSound();
    }
    @Override
    public void tick() {
        super.tick();
        if (soundCD > 0) soundCD--;
    }

    @Override
    public void onHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        toHeal -= event.getAmount();
        if (toHeal <= 0) EffectCleansing.forceCleanse(getOwner(), FTZMobEffects.HAEMORRHAGE.get());
    }
    @Override
    public void onHit(LivingHurtEvent event) {
        if (event.getSource().is(FTZDamageTypes.PARRY)) EffectHelper.giveHaemorrhage(getOwner(), 200);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        getOwner().level().playSound(null, getOwner().blockPosition(), FTZSoundEvents.FLESH_RIPPING.get(), SoundSource.NEUTRAL,0.35f,1f);
        FTZDamageTypes.DamageSources sources = LevelCapHelper.getDamageSources(getOwner().level());
        if (sources != null) getOwner().hurt(sources.bleeding(), getOwner().getHealth() * 0.1f);
        toHeal = 4 + 2 * instance.getAmplifier();
    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = super.serialize(toDisk);
        if (toDisk) return tag;
        tag.putInt("soundCD", soundCD);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        super.deserialize(tag, fromDisk);
        if (fromDisk) return;
        soundCD = tag.contains("soundCD") ? tag.getInt("soundCD") : 0;
    }
    @Override
    public boolean unSyncedDuration() {
        return true;
    }
}
