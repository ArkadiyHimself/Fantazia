package net.arkadiyhimself.fantazia.api.capability;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public interface IDamageReacting {
    default void onHit(LivingHurtEvent event) {

    }
    default void onHit(LivingAttackEvent event) {

    }
    default void onHit(LivingDamageEvent event) {

    }
}
