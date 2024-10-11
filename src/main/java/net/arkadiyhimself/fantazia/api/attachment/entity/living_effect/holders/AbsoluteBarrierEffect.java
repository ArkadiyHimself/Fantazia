package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class AbsoluteBarrierEffect extends LivingEffectHolder implements IDamageEventListener {
    public AbsoluteBarrierEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("absolute_barrier_effect"), FTZMobEffects.ABSOLUTE_BARRIER);
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        if (duration() > 0 && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) event.setCanceled(true);
    }

    public boolean hasBarrier() {
        return duration() > 0;
    }
}
