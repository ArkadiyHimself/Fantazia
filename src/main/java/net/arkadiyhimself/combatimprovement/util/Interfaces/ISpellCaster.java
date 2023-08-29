package net.arkadiyhimself.combatimprovement.util.Interfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public interface ISpellCaster {
    SoundEvent getCastSound();
    boolean hasCooldown(ServerPlayer player);
    boolean conditionNotMet(ServerPlayer player);
    boolean targetConditions(ServerPlayer player, LivingEntity target);
    default void activeAbility(ServerPlayer player) {}
    default void targetedAbility(@Nullable ServerPlayer player, LivingEntity target) {}
    default void retarget(LivingEntity originalCaster) {}
}
