package net.arkadiyhimself.fantazia.util.Interfaces;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public interface IDamageReacting {
    default void onHit(LivingAttackEvent event) {}
    default void onHit(LivingHurtEvent event) {}
    default void onHit(LivingDamageEvent event) {}
}
