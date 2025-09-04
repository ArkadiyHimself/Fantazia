package net.arkadiyhimself.fantazia.common.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEffectComponentTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.core.time.MutableInstant;

public record Amplification(LevelBasedValue value) {

    public static final Codec<Amplification> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amplify").forGetter(Amplification::value)
    ).apply(instance, Amplification::new));

    public int getLevel(int enchantmentLevel) {
        return Mth.floor(value.calculate(enchantmentLevel));
    }

    public static int getAmplifier(ItemStack stack) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.runIterationOnItem(stack, (holder, level) -> {
            Enchantment enchantment = holder.value();
            for (Amplification amplification : enchantment.getEffects(FTZEnchantmentEffectComponentTypes.AMPLIFICATION_LEVEL.value()))
                mutableInt.add(amplification.getLevel(level));
        });
        return mutableInt.intValue();
    }
}
