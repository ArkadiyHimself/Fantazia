package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class FrozenEffect extends LivingEffectHolder {
    public FrozenEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("frozen_effect"), FTZMobEffects.FROZEN);
    }
    public float freezePercent() {
        return getEntity().getPercentFrozen();
    }
    public float effectPercent() {
        return (float) duration / ((float) initialDur);
    }
    public boolean renderFreeze() {
        return freezePercent() > 0 || effectPercent() > 0;
    }
}
