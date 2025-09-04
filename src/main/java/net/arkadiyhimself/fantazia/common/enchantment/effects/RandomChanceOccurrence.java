package net.arkadiyhimself.fantazia.common.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record RandomChanceOccurrence(LevelBasedValue chance) {

    public static final Codec<RandomChanceOccurrence> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("chance").forGetter(RandomChanceOccurrence::chance)
    ).apply(instance, RandomChanceOccurrence::new));

    public boolean attempt(int enchantmentLevel, RandomSource source) {
        float i = chance.calculate(enchantmentLevel);
        return source.nextFloat() <= i;
    }
}
