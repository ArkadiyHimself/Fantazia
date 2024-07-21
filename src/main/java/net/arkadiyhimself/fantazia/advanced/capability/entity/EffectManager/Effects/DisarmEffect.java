package net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectHolder;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class DisarmEffect extends EffectHolder {
    public DisarmEffect(LivingEntity owner) {
        super(owner, MobEffectRegistry.DISARM.get());
    }
    public boolean renderDisarm() {
        return getDur() > 0;
    }
}
