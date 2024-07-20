package net.arkadiyhimself.fantazia.util.Interfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

public interface IPassiveCaster {
    SoundEvent getCastSound();
    boolean hasCooldown(ServerPlayer player);
    boolean conditionNotMet(ServerPlayer player);
    default void passiveAbility(ServerPlayer player) {}
}
