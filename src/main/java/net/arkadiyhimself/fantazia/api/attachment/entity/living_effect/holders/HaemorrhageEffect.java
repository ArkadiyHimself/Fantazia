package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.type.entity.IHealListener;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class HaemorrhageEffect extends LivingEffectHolder implements IDamageEventListener, IHealListener {
    private float toHeal = 0;
    private int soundCD = 0;
    public HaemorrhageEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("haemorrhage_effect"), FTZMobEffects.HAEMORRHAGE);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("soundCD", soundCD);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        soundCD = compoundTag.contains("soundCD") ? compoundTag.getInt("soundCD") : 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (soundCD > 0) soundCD--;
    }

    @Override
    public void onHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        toHeal -= event.getAmount();
        if (toHeal <= 0) EffectCleansing.forceCleanse(getEntity(), FTZMobEffects.HAEMORRHAGE);
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        if (event.getSource().is(FTZDamageTypes.PARRY)) LivingEffectHelper.giveHaemorrhage(getEntity(), 200);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.FLESH_RIPPING.get(), SoundSource.NEUTRAL,0.35f,1f);
        DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(getEntity().level());
        if (sources != null) getEntity().hurt(sources.bleeding(), getEntity().getHealth() * 0.1f);
        toHeal = 4 + 2 * instance.getAmplifier();
    }

    private boolean shouldEmitSound() {
        return soundCD < 0;
    }

    private void emitSound() {
        soundCD = 10;
        getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.BLOODLOSS.get(), SoundSource.HOSTILE);
    }

    public void tryMakeSound() {
        if (shouldEmitSound()) emitSound();
    }
}
