package net.arkadiyhimself.fantazia.common.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEffectComponentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ParryModify(
        Optional<LevelBasedValue> addInitial,
        Optional<LevelBasedValue> multiply,
        Optional<LevelBasedValue> addTotal
) {

    public static final Codec<ParryModify> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.optionalFieldOf("add_initial").forGetter(ParryModify::addInitial),
            LevelBasedValue.CODEC.optionalFieldOf("multiply").forGetter(ParryModify::multiply),
            LevelBasedValue.CODEC.optionalFieldOf("add_total").forGetter(ParryModify::addTotal)
    ).apply(instance, ParryModify::new));

    public ParryModify(@Nullable LevelBasedValue addInitial, @Nullable LevelBasedValue multiply, @Nullable LevelBasedValue addTotal) {
        this(Optional.ofNullable(addInitial), Optional.ofNullable(multiply), Optional.ofNullable(addTotal));
    }

    public static ParryModify addInitial(LevelBasedValue value) {
        return new ParryModify(value, null, null);
    }

    public static ParryModify multiply(LevelBasedValue value) {
        return new ParryModify(null, value, null);
    }

    public static ParryModify addTotal(LevelBasedValue value) {
        return new ParryModify(null, null, value);
    }

    public void process(MutableFloat initial, int enchantmentLevel) {
        float value = initial.floatValue();
        if (addInitial.isPresent()) value += addInitial.get().calculate(enchantmentLevel);
        if (multiply.isPresent()) value *= multiply.get().calculate(enchantmentLevel);
        if (addTotal.isPresent()) value += addTotal.get().calculate(enchantmentLevel);
        initial.setValue(value);
    }

    public static float modifyParry(ServerLevel serverLevel, ItemStack weapon, Entity target, DamageSource source, float initial) {
        MutableFloat mutableFloat = new MutableFloat(initial);
        EnchantmentHelper.runIterationOnItem(weapon, (holder, level) -> {
            LootContext lootContext = Enchantment.damageContext(serverLevel, level, target, source);
            Enchantment enchantment = holder.value();
            for (TargetedConditionalEffect<ParryModify> conditionalEffect : enchantment.getEffects(FTZEnchantmentEffectComponentTypes.PARRY_MODIFY.value())) {
                if (conditionalEffect.matches(lootContext)) conditionalEffect.effect().process(mutableFloat, level);
            }
        });
        return mutableFloat.floatValue();
    }
}
