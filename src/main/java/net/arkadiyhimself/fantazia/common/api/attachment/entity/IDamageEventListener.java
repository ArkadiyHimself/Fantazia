package net.arkadiyhimself.fantazia.common.api.attachment.entity;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public interface IDamageEventListener {

    default void onHit(LivingIncomingDamageEvent event) {}

    default void onHit(LivingDamageEvent.Pre event) {}

    default void onHit(LivingDamageEvent.Post event) {}
}
