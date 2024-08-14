package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class DisarmEffect extends EffectHolder {
    @SuppressWarnings("ConstantConditions")
    public DisarmEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.DISARM);
    }
    public boolean renderDisarm() {
        return getDur() > 0;
    }
}
