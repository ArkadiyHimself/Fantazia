package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class CursedMarkEffect extends EffectHolder {
    public CursedMarkEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.CURSED_MARK.get());
    }
    public boolean isMarked() {
        return getDur() > 0;
    }
}
