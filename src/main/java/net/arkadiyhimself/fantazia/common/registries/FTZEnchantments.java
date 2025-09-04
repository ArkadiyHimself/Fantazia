package net.arkadiyhimself.fantazia.common.registries;


import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.enchantment.effects.*;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEffectComponentTypes;
import net.arkadiyhimself.fantazia.data.tags.FTZEnchantmentTags;
import net.arkadiyhimself.fantazia.data.tags.FTZEntityTypeTags;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public interface FTZEnchantments {

    ResourceKey<Enchantment> DISINTEGRATION = key("disintegration");
    ResourceKey<Enchantment> ICE_ASPECT = key("ice_aspect");
    ResourceKey<Enchantment> FREEZE = key("freeze");
    ResourceKey<Enchantment> DECISIVE_STRIKE = key("decisive_strike");
    ResourceKey<Enchantment> BULLY = key("bully");
    // crossbows
    ResourceKey<Enchantment> DUELIST = key("duelist");
    ResourceKey<Enchantment> BALLISTA = key("ballista");
    // hatchets
    ResourceKey<Enchantment> PHASING = key("phasing");
    ResourceKey<Enchantment> RICOCHET = key("ricochet");
    ResourceKey<Enchantment> BULLSEYE = key("bullseye");
    // amplification bench exclusive
    ResourceKey<Enchantment> AMPLIFICATION = key("amplification");
    ResourceKey<Enchantment> SCORCHED_EARTH = key("scorched_earth");
    ResourceKey<Enchantment> INCINERATION = key("incineration");
    ResourceKey<Enchantment> EXECUTIONER = key("executioner");

    static void bootStrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantmentHolderGetter = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemHolderGetter = context.lookup(Registries.ITEM);

        register(context, DISINTEGRATION, Enchantment.enchantment(
                        Enchantment.definition(
                                itemHolderGetter.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                2,
                                3,
                                Enchantment.dynamicCost(15, 9),
                                Enchantment.dynamicCost(65, 9),
                                4,
                                EquipmentSlotGroup.MAINHAND
                        )
                ).withEffect(
                        FTZEnchantmentEffectComponentTypes.EQUIPMENT_CONVERT.value(),
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new ConvertLootToExp(
                                LevelBasedValue.perLevel(3F, 2F),
                                null,
                                ItemPredicate.Builder.item().hasComponents(
                                        DataComponentPredicate.builder().expect(FTZDataComponentTypes.DISINTEGRATE.value(), false).build()
                                ).build(),
                                TagPredicate.isNot(FTZItemTags.NO_DISINTEGRATION)
                        )
                ).withEffect(
                        EnchantmentEffectComponents.MOB_EXPERIENCE,
                        new MultiplyValue(LevelBasedValue.perLevel(2F, 0.5F))
                )
                .exclusiveWith(enchantmentHolderGetter.getOrThrow(Tags.Enchantments.INCREASE_ENTITY_DROPS)));

        register(context, ICE_ASPECT, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.FIRE_ASPECT_ENCHANTABLE),
                                        2,
                                        2,
                                        Enchantment.dynamicCost(10, 20),
                                        Enchantment.dynamicCost(60, 20),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        ).withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.VICTIM,
                                new ApplyMobEffect(
                                        HolderSet.direct(FTZMobEffects.FROZEN),
                                        LevelBasedValue.perLevel(4F),
                                        LevelBasedValue.perLevel(4F),
                                        LevelBasedValue.constant(0f),
                                        LevelBasedValue.constant(0F)
                                ),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))
                        )
                        .exclusiveWith(HolderSet.direct(
                                        enchantmentHolderGetter.getOrThrow(
                                                Enchantments.FIRE_ASPECT
                                        )
                                )
                        )
        );

        register(context, FREEZE, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        2,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        ).withEffect(
                                EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                                new ApplyMobEffect(
                                        HolderSet.direct(FTZMobEffects.FROZEN),
                                        LevelBasedValue.perLevel(5F),
                                        LevelBasedValue.perLevel(5F),
                                        LevelBasedValue.constant(0f),
                                        LevelBasedValue.constant(0F)
                                )
                        )
                        .exclusiveWith(
                                HolderSet.direct(enchantmentHolderGetter.getOrThrow(Enchantments.FLAME))
                        )
        );

        register(context, DECISIVE_STRIKE, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                        2,
                        3,
                        Enchantment.dynamicCost(5, 9),
                        Enchantment.dynamicCost(20, 9),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )
        ).withEffect(
                FTZEnchantmentEffectComponentTypes.CRITICAL_DAMAGE_MODIFY.value(),
                EnchantmentTarget.ATTACKER,
                EnchantmentTarget.VICTIM,
                CriticalStrikeModify.addInitial(LevelBasedValue.perLevel(0.5F),true)
        ).withEffect(
                FTZEnchantmentEffectComponentTypes.PARRY_MODIFY.value(),
                EnchantmentTarget.ATTACKER,
                EnchantmentTarget.VICTIM,
                ParryModify.addInitial(LevelBasedValue.perLevel(0.5F))
        ).exclusiveWith(HolderSet.direct(enchantmentHolderGetter.getOrThrow(Enchantments.SWEEPING_EDGE))));

        register(context, BULLY, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                                        itemHolderGetter.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        5,
                                        5,
                                        Enchantment.dynamicCost(5, 8),
                                        Enchantment.dynamicCost(25, 8),
                                        2,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        ).withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(3)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().effects(
                                                MobEffectsPredicate.Builder.effects().and(FTZMobEffects.STUN)
                                        )
                                )
                        )
                        .exclusiveWith(
                                enchantmentHolderGetter.getOrThrow(
                                        EnchantmentTags.DAMAGE_EXCLUSIVE
                                )
                        )
        );

        // crossbows
        register(context, DUELIST, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                                        2,
                                        5,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        ).withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(0.5f, 0.75f)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity()
                                                .entityType(
                                                        EntityTypePredicate.of(
                                                                FTZEntityTypeTags.RANGED_ATTACK
                                                        )
                                                )
                                ).or(
                                        LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity()
                                                        .equipment(
                                                                EntityEquipmentPredicate.Builder.equipment().mainhand(
                                                                        ItemPredicate.Builder.item().of(Tags.Items.RANGED_WEAPON_TOOLS)
                                                                )
                                                        )
                                        )
                                ).and(
                                        LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                                EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
                                        )
                                )
                        )
                        .exclusiveWith(
                                enchantmentHolderGetter.getOrThrow(
                                        FTZEnchantmentTags.CROSSBOW_DAMAGE_EXCLUSIVE
                                )
                        )
        );

        register(context, BALLISTA, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        itemHolderGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        2,
                        5,
                        Enchantment.constantCost(20),
                        Enchantment.constantCost(50),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )
        ).withEffect(
                        EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(0.5f, 0.75f)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity()
                                        .entityType(
                                                EntityTypePredicate.of(
                                                        FTZEntityTypeTags.AERIAL
                                                )
                                        )
                        ).or(
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(
                                                EntityFlagsPredicate.Builder.flags().setIsFlying(true)
                                        )
                                )
                        ).or(
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(
                                                EntityFlagsPredicate.Builder.flags().setOnGround(false)
                                        )
                                )
                        ).and(
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
                                )
                        )
                )
                .exclusiveWith(
                        enchantmentHolderGetter.getOrThrow(
                                FTZEnchantmentTags.CROSSBOW_DAMAGE_EXCLUSIVE
                        )
                )
        );

        // hatchet behaviour
        register(context, PHASING, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(FTZItemTags.HATCHET_ENCHANTABLE),
                        2,
                        3,
                        Enchantment.constantCost(20),
                        Enchantment.constantCost(50),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )
        ).withEffect(
                EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                new HatchetPhasing(LevelBasedValue.perLevel(1F, 0.5F))
        ).exclusiveWith(enchantmentHolderGetter.getOrThrow(FTZEnchantmentTags.HATCHET_BEHAVIOUR_EXCLUSIVE)));

        register(context, RICOCHET, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(FTZItemTags.HATCHET_ENCHANTABLE),
                        2,
                        3,
                        Enchantment.constantCost(20),
                        Enchantment.constantCost(50),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )
        ).withEffect(
                EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                new HatchetRicochet(LevelBasedValue.perLevel(1F))
        ).exclusiveWith(enchantmentHolderGetter.getOrThrow(FTZEnchantmentTags.HATCHET_BEHAVIOUR_EXCLUSIVE)));

        register(context, BULLSEYE, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(FTZItemTags.HATCHET_ENCHANTABLE),
                        2,
                        5,
                        Enchantment.constantCost(20),
                        Enchantment.constantCost(50),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )
        ).withEffect(
                EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                new HatchetHeadshot(LevelBasedValue.perLevel(3F))
        ));

        // amplification bench
        register(context, AMPLIFICATION, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(FTZItemTags.CASTER_ENCHANTABLE),
                        3,
                        3,
                        Enchantment.constantCost(18),
                        Enchantment.constantCost(54),
                        4
                )
        ).withSpecialEffect(
                FTZEnchantmentEffectComponentTypes.AMPLIFICATION_LEVEL.value(),
                List.of(new Amplification(LevelBasedValue.perLevel(1F)))
        ));

        register(context, SCORCHED_EARTH, Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(FTZItemTags.ANCIENT_FLAME_ENCHANTABLE),
                                        4,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        8,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        ).withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.VICTIM,
                                new Combust(LevelBasedValue.constant(6f)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))
                        )
                        .exclusiveWith(HolderSet.direct(
                                        enchantmentHolderGetter.getOrThrow(Enchantments.FIRE_ASPECT),
                                        enchantmentHolderGetter.getOrThrow(FTZEnchantments.ICE_ASPECT)
                                )
                        )
        );

        register(context, INCINERATION, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        4,
                        1,
                        Enchantment.constantCost(20),
                        Enchantment.constantCost(50),
                        8
                )
                        ).withEffect(
                                EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                                new Combust(LevelBasedValue.constant(100F))
                        )
                        .exclusiveWith(
                                HolderSet.direct(enchantmentHolderGetter.getOrThrow(Enchantments.FLAME),
                                        enchantmentHolderGetter.getOrThrow(FTZEnchantments.FREEZE)
                                )
                        )
        );

        register(context, EXECUTIONER, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(FTZItemTags.AXE_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.constantCost(10),
                        Enchantment.constantCost(30),
                        8
                )
        ).withEffect(
                FTZEnchantmentEffectComponentTypes.DECAPITATION.value(),
                EnchantmentTarget.ATTACKER,
                EnchantmentTarget.VICTIM,
                new RandomChanceOccurrence(LevelBasedValue.perLevel(0.2F))
        ));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private static ResourceKey<Enchantment> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT, Fantazia.location(pName));
    }
}
