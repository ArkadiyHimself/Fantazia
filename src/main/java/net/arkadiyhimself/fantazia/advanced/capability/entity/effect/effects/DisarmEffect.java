package net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class DisarmEffect extends EffectHolder {
    public DisarmEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.DISARM);
    }
    public boolean renderDisarm() {
        return getDur() > 0;
    }
}
