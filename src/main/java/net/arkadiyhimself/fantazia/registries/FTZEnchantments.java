package net.arkadiyhimself.fantazia.registries;


import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.tags.FTZEnchantmentTags;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;

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
    ResourceKey<Enchantment> HEADSHOT = key("headshot");

    static void bootStrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantmentHolderGetter = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemHolderGetter = context.lookup(Registries.ITEM);

        register(context, BALLISTA, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        2,
                        5,
                        Enchantment.constantCost(20),
                        Enchantment.constantCost(50),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )
        ).exclusiveWith(enchantmentHolderGetter.getOrThrow(FTZEnchantmentTags.CROSSBOW_DAMAGE_EXCLUSIVE)));

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
        ).exclusiveWith(enchantmentHolderGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE)));

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
        ).exclusiveWith(HolderSet.direct(enchantmentHolderGetter.getOrThrow(Enchantments.SWEEPING_EDGE))));

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
        ).exclusiveWith(enchantmentHolderGetter.getOrThrow(Tags.Enchantments.INCREASE_ENTITY_DROPS)));

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
        ).exclusiveWith(enchantmentHolderGetter.getOrThrow(FTZEnchantmentTags.CROSSBOW_DAMAGE_EXCLUSIVE)));

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
        ).exclusiveWith(HolderSet.direct(enchantmentHolderGetter.getOrThrow(Enchantments.FLAME))));

        register(context, HEADSHOT, Enchantment.enchantment(
                Enchantment.definition(
                        itemHolderGetter.getOrThrow(FTZItemTags.HATCHET_ENCHANTABLE),
                        2,
                        5,
                        Enchantment.constantCost(20),
                        Enchantment.constantCost(50),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )
        ));

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
        ).exclusiveWith(HolderSet.direct(enchantmentHolderGetter.getOrThrow(Enchantments.FIRE_ASPECT))));

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
        ).exclusiveWith(enchantmentHolderGetter.getOrThrow(FTZEnchantmentTags.HATCHET_BEHAVIOUR_EXCLUSIVE)));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private static ResourceKey<Enchantment> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT, Fantazia.res(pName));
    }
}
