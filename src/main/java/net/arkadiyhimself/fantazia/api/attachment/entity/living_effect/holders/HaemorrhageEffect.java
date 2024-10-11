package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.custom_events.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.type.entity.IHealEventListener;
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

public class HaemorrhageEffect extends LivingEffectHolder implements IDamageEventListener, IHealEventListener {
    private float toHeal = 0;
    public HaemorrhageEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("haemorrhage_effect"), FTZMobEffects.HAEMORRHAGE);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putFloat("toHeal", toHeal);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        toHeal = compoundTag.getFloat("toHeal");
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
        getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EFFECT_HAEMORRHAGE_FLESH_RIPPING.get(), SoundSource.NEUTRAL,0.35f,1f);
        DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(getEntity().level());
        if (sources != null) getEntity().hurt(sources.bleeding(), getEntity().getHealth() * 0.1f);
        toHeal = 4 + 2 * instance.getAmplifier();
    }

    public void emitSound() {
        getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EFFECT_HAEMORRHAGE_BLOODLOSS.get(), SoundSource.HOSTILE);
    }

}
