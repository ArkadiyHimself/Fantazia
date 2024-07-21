package net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectHolder;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.minecraft.world.entity.LivingEntity;

public class FrozenEffect extends EffectHolder {
    public FrozenEffect(LivingEntity owner) {
        super(owner, MobEffectRegistry.FROZEN.get());
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
