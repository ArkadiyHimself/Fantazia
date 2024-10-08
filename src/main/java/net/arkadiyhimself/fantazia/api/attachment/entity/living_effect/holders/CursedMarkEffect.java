package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class CursedMarkEffect extends LivingEffectHolder {
    public CursedMarkEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("cursed_mark_effect"), FTZMobEffects.CURSED_MARK);
    }
    public boolean isMarked() {
        return duration() > 0;
    }
}
