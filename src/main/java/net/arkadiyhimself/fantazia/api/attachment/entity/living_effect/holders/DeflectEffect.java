package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class DeflectEffect extends LivingEffectHolder {

    public DeflectEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("deflect"), FTZMobEffects.DEFLECT);
    }

    public boolean hasDeflect() {
        return duration() > 0;
    }
}
