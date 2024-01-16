package net.arkadiyhimself.combatimprovement.util.Interfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface ISpellCaster {
    SoundEvent getCastSound();
    boolean hasCooldown(ServerPlayer player);
    boolean conditionNotMet(ServerPlayer player);
    boolean targetConditions(ServerPlayer player, LivingEntity target);
    default void activeAbility(ServerPlayer player) {}
    boolean targetedAbility(@Nullable LivingEntity player, LivingEntity target, boolean deflect);
}
