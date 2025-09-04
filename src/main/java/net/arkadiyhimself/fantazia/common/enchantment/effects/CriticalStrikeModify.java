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

public record CriticalStrikeModify(
        Optional<LevelBasedValue> addInitial,
        Optional<LevelBasedValue> multiply,
        Optional<LevelBasedValue> addTotal,
        Optional<Boolean> isVanilla
        ) {

    public static final Codec<CriticalStrikeModify> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.optionalFieldOf("add_initial").forGetter(CriticalStrikeModify::addInitial),
            LevelBasedValue.CODEC.optionalFieldOf("multiply").forGetter(CriticalStrikeModify::multiply),
            LevelBasedValue.CODEC.optionalFieldOf("add_total").forGetter(CriticalStrikeModify::addTotal),
            Codec.BOOL.optionalFieldOf("is_vanilla").forGetter(CriticalStrikeModify::isVanilla)
            ).apply(instance, CriticalStrikeModify::new));

    public CriticalStrikeModify(@Nullable LevelBasedValue addInitial, @Nullable LevelBasedValue multiply, @Nullable LevelBasedValue addTotal, @Nullable Boolean isVanilla) {
        this(Optional.ofNullable(addInitial), Optional.ofNullable(multiply), Optional.ofNullable(addTotal), Optional.ofNullable(isVanilla));
    }

    public static CriticalStrikeModify addInitial(LevelBasedValue value, @Nullable Boolean isVanilla) {
        return new CriticalStrikeModify(value, null, null, isVanilla);
    }

    public static CriticalStrikeModify multiply(LevelBasedValue value, @Nullable Boolean isVanilla) {
        return new CriticalStrikeModify(null, value, null, isVanilla);
    }

    public static CriticalStrikeModify addTotal(LevelBasedValue value, @Nullable Boolean isVanilla) {
        return new CriticalStrikeModify(null, null, value, isVanilla);
    }

    public void process(MutableFloat initial, int enchantmentLevel, boolean vanilla) {
        if (isVanilla.isPresent() && isVanilla.get() != vanilla) return;
        float value = initial.floatValue();
        if (addInitial.isPresent()) value += addInitial.get().calculate(enchantmentLevel);
        if (multiply.isPresent()) value *= multiply.get().calculate(enchantmentLevel);
        if (addTotal.isPresent()) value += addTotal.get().calculate(enchantmentLevel);
        initial.setValue(value);
    }

    public @NotNull Codec<? extends CriticalStrikeModify> codec() {
        return CODEC;
    }

    public static float modifyCriticalStrike(ServerLevel serverLevel, ItemStack weapon, Entity target, DamageSource source, float initial, boolean vanilla) {
        MutableFloat mutableFloat = new MutableFloat(initial);
        EnchantmentHelper.runIterationOnItem(weapon, (holder, level) -> {
            LootContext lootContext = Enchantment.damageContext(serverLevel, level, target, source);
            Enchantment enchantment = holder.value();
            for (TargetedConditionalEffect<CriticalStrikeModify> conditionalEffect : enchantment.getEffects(FTZEnchantmentEffectComponentTypes.CRITICAL_DAMAGE_MODIFY.value())) {
                if (conditionalEffect.matches(lootContext)) conditionalEffect.effect().process(mutableFloat, level, vanilla);
            }
        });
        return mutableFloat.floatValue();
    }
}
