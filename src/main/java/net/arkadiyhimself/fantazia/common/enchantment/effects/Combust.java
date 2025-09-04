package net.arkadiyhimself.fantazia.common.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record Combust(LevelBasedValue duration) implements EnchantmentEntityEffect {

    public static final MapCodec<Combust> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(Combust::duration)
    ).apply(instance, Combust::new));

    public void apply(@NotNull ServerLevel level, int enchantmentLevel, @NotNull EnchantedItemInUse item, Entity entity, @NotNull Vec3 origin) {
        entity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).set(Mth.floor(20f * this.duration.calculate(enchantmentLevel)));
    }

    public @NotNull MapCodec<Combust> codec() {
        return CODEC;
    }
}
