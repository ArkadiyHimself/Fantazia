package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.IHealReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class HaemorrhageEffect extends EffectHolder implements IDamageReacting, IHealReacting {
    private float toHeal = 0;
    private int soundCD = 0;
    @SuppressWarnings("ConstantConditions")
    public HaemorrhageEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.HAEMORRHAGE);
    }

    private boolean shouldEmitSound() {
        return soundCD < 0;
    }
    private void emitSound() {
        soundCD = 10;
        getOwner().playSound(FTZSoundEvents.BLOODLOSS);
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
        if (toHeal <= 0) EffectCleansing.forceCleanse(getOwner(), FTZMobEffects.HAEMORRHAGE);
    }
    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        getOwner().level().playSound(null, getOwner().blockPosition(), FTZSoundEvents.FLESH_RIPPING, SoundSource.NEUTRAL,0.35f,1f);
        getOwner().hurt(new DamageSource(getOwner().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FTZDamageTypes.BLEEDING)), getOwner().getHealth() * 0.1f);
        toHeal = 4 + 2 * instance.getAmplifier();
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "soundCD", soundCD);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        soundCD = tag.contains(ID + "soundCD") ? tag.getInt(ID + "soundCD") : 0;
    }
}
