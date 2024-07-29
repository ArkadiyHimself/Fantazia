package net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class FrozenEffect extends EffectHolder {
    public FrozenEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.FROZEN);
    }
    public float freezePercent() {
        return getOwner().getPercentFrozen();
    }
    public float effectPercent() {
        return (float) duration / (float) INITIAL_DUR;
    }
    public boolean renderFreeze() {
        return freezePercent() > 0 || effectPercent() > 0;
    }
}
