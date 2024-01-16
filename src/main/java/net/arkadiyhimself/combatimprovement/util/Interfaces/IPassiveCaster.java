package net.arkadiyhimself.combatimprovement.util.Interfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface IPassiveCaster {
    SoundEvent getCastSound();
    boolean hasCooldown(ServerPlayer player);
    boolean conditionNotMet(ServerPlayer player);
    default void passiveAbility(ServerPlayer player) {}
}
