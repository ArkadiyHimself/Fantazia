package net.arkadiyhimself.fantazia.common.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEffectComponentTypes;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record ConvertLootToExp(
        LevelBasedValue value,
        Optional<ItemPredicate> regularPredicate,
        Optional<ItemPredicate> reversedPredicate,
        List<TagPredicate<Item>> tagPredicates
) {

    public static final Codec<ConvertLootToExp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("value").forGetter(ConvertLootToExp::value),
            ItemPredicate.CODEC.optionalFieldOf("regular_predicate").forGetter(ConvertLootToExp::regularPredicate),
            ItemPredicate.CODEC.optionalFieldOf("reversed_predicate").forGetter(ConvertLootToExp::reversedPredicate),
            TagPredicate.codec(Registries.ITEM).listOf().optionalFieldOf("tag", Lists.newArrayList()).forGetter(ConvertLootToExp::tagPredicates)
    ).apply(instance, ConvertLootToExp::new));

    @SafeVarargs
    public ConvertLootToExp(LevelBasedValue value, @Nullable ItemPredicate predicate, @Nullable ItemPredicate reversed, @Nullable TagPredicate<Item>... ignored) {
        this(value, Optional.ofNullable(predicate), Optional.ofNullable(reversed), List.of(ignored));
    }

    public ConvertLootToExp(LevelBasedValue value, @Nullable ItemPredicate predicate, @Nullable ItemPredicate reversed) {
        this(value, Optional.ofNullable(predicate), Optional.ofNullable(reversed), List.of());
    }

    public ConvertLootToExp(LevelBasedValue value, @Nullable ItemPredicate predicate) {
        this(value, Optional.ofNullable(predicate), Optional.empty(), List.of());
    }

    public boolean process(ItemStack stack, LivingEntity killed, int level) {
        if (regularPredicate.isPresent() && !regularPredicate.get().test(stack)) return false;
        if (reversedPredicate.isPresent() && reversedPredicate.get().test(stack)) return false;
        for (TagPredicate<Item> predicate : tagPredicates) if (!predicate.matches(stack.getItemHolder())) return false;

        float multiplier = switch (stack.getRarity()) {
            case COMMON -> 1f;
            case UNCOMMON -> 1.25f;
            case RARE -> 1.75f;
            case EPIC -> 2.5f;
        };

        if (killed.level() instanceof ServerLevel serverLevel)
            ExperienceOrb.award(serverLevel, killed.position(), (int) (value.calculate(level) * multiplier * stack.getCount()));

        return true;
    }

    public static boolean convertLootToExp(ServerLevel serverLevel, ItemStack weapon, ItemStack converted, LivingEntity target, DamageSource source) {
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        EnchantmentHelper.runIterationOnItem(weapon, (holder, level) -> {
            LootContext lootContext = Enchantment.damageContext(serverLevel, level, target, source);
            Enchantment enchantment = holder.value();
            for (TargetedConditionalEffect<ConvertLootToExp> conditionalEffect : enchantment.getEffects(FTZEnchantmentEffectComponentTypes.EQUIPMENT_CONVERT.value())) {
                if (conditionalEffect.matches(lootContext) && conditionalEffect.effect().process(converted, target, level)) mutableBoolean.setTrue();
            }
        });

        return mutableBoolean.booleanValue();
    }
}
