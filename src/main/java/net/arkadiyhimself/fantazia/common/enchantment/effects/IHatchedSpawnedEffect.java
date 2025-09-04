package net.arkadiyhimself.fantazia.common.enchantment.effects;

import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface IHatchedSpawnedEffect extends EnchantmentEntityEffect {

    @Override
    default void apply(@NotNull ServerLevel serverLevel, int enchantmentLevel, @NotNull EnchantedItemInUse enchantedItemInUse, @NotNull Entity entity, @NotNull Vec3 vec3) {
        if (entity instanceof ThrownHatchet thrownHatchet) apply(serverLevel, enchantmentLevel, enchantedItemInUse, thrownHatchet, vec3);
    }

    void apply(@NotNull ServerLevel serverLevel, int enchantmentLevel, @NotNull EnchantedItemInUse enchantedItemInUse, @NotNull ThrownHatchet thrownHatchet, Vec3 vec3);
}
