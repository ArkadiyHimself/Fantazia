package net.arkadiyhimself.fantazia.common.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record HatchetRicochet(LevelBasedValue value) implements IHatchedSpawnedEffect {

    public static final MapCodec<HatchetRicochet> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(HatchetRicochet::value)
    ).apply(instance, HatchetRicochet::new));

    @Override
    public void apply(@NotNull ServerLevel serverLevel, int enchantmentLevel, @NotNull EnchantedItemInUse enchantedItemInUse, @NotNull ThrownHatchet thrownHatchet, Vec3 vec3) {
        int ricochets = Mth.floor(value.calculate(enchantmentLevel));
        thrownHatchet.setRicochets(ricochets);
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
