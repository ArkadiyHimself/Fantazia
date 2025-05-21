package net.arkadiyhimself.fantazia.datagen.loot_modifier;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.loot.LootInstance;
import net.arkadiyhimself.fantazia.data.loot.LootModifier;
import net.arkadiyhimself.fantazia.datagen.DynamicResourceLocation;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.arkadiyhimself.fantazia.items.RuneWielderItem;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.custom.Runes;
import net.arkadiyhimself.fantazia.util.wheremagichappens.LootTablesUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;

public class DefaultFantazicLootModifiers implements SubProvider<LootModifierHolder> {

    public static DefaultFantazicLootModifiers create() {
        return new DefaultFantazicLootModifiers();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<LootModifierHolder> consumer) {
        DynamicResourceLocation ancientCity = builtInLootTable(BuiltInLootTables.ANCIENT_CITY);
        LootModifier.builder()
                .addLootTables(ancientCity.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.HEART_OF_SCULK.value(), 0.15))
                .addLootInstance(LootInstance.Builder.of(FTZItems.MYSTIC_MIRROR.value(), 0.08))
                .addLootInstance(LootInstance.Builder.of(FTZItems.VITALITY_FRUIT.value(), 1.0, true))
                .addLootInstance(LootInstance.Builder.of(RuneWielderItem.rune(Runes.NOISELESS), 0.175))
                .save(consumer, ancientCity.fantazia());

        DynamicResourceLocation blaze = entityLootTable(EntityType.BLAZE);
        LootModifier.builder()
                .addLootTables(blaze.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.NETHER_HEART.value(), 0.175, true))
                .save(consumer, blaze.fantazia());

        DynamicResourceLocation elderGuardian = entityLootTable(EntityType.ELDER_GUARDIAN);
        LootModifier.builder()
                .addLootTables(elderGuardian.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.INSIGHT_ESSENCE.value(), 1, true))
                .save(consumer, elderGuardian.fantazia());

        DynamicResourceLocation endCity = builtInLootTable(BuiltInLootTables.END_CITY_TREASURE);
        LootModifier.builder()
                .addLootTables(endCity.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.VITALITY_FRUIT.value(), 1, true))
                .save(consumer, endCity.fantazia());

        DynamicResourceLocation enderMan = entityLootTable(EntityType.ENDERMAN);
        LootModifier.builder()
                .addLootTables(endCity.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.SPIRAL_NEMESIS.value(), 0.075, true))
                .addLootInstance(LootInstance.Builder.of(FTZItems.INSIGHT_ESSENCE.value(), 1, true))
                .save(consumer, enderMan.fantazia());

        DynamicResourceLocation evoker = entityLootTable(EntityType.EVOKER);
        LootModifier.builder()
                .addLootTables(evoker.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.INSIGHT_ESSENCE.value(), 1, true))
                .save(consumer, evoker.fantazia());

        DynamicResourceLocation mineshaft = builtInLootTable(BuiltInLootTables.ABANDONED_MINESHAFT);
        LootModifier.builder()
                .addLootTables(mineshaft.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.TRANQUIL_HERB.value(), 0.25))
                .addLootInstance(LootInstance.Builder.of(RuneWielderItem.rune(Runes.PROSPERITY), 0.1125))
                .save(consumer, mineshaft.fantazia());

        LootModifier.builder()
                .addLootTables(LootTablesUtil.nether())
                .addLootInstance(LootInstance.Builder.of(FTZItems.BLOODLUST_AMULET.value(), 0.085))
                .addLootInstance(LootInstance.Builder.of(FTZItems.SOUL_EATER.value(), 0.06))
                .addLootInstance(LootInstance.Builder.of(FTZItems.VITALITY_FRUIT.value(), 1.0, true))
                .save(consumer, Fantazia.res("nether"));

        LootModifier.builder()
                .addLootTables(LootTablesUtil.ocean())
                .addLootInstance(LootInstance.Builder.of(FTZItems.CAUGHT_THUNDER.value(), 0.125))
                .save(consumer, Fantazia.res("ocean"));

        DynamicResourceLocation piglinBrute = entityLootTable(EntityType.PIGLIN_BRUTE);
        LootModifier.builder()
                .addLootTables(piglinBrute.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.INSIGHT_ESSENCE.value(), 1, true))
                .save(consumer, piglinBrute.fantazia());

        DynamicResourceLocation pillagerOutpost = builtInLootTable(BuiltInLootTables.PILLAGER_OUTPOST);
        LootModifier.builder()
                .addLootTables(pillagerOutpost.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.LEADERS_HORN.value(),0.15, Items.GOAT_HORN))
                .addLootInstance(LootInstance.Builder.of(RuneWielderItem.rune(Runes.PIERCER), 0.3))
                .save(consumer, pillagerOutpost.fantazia());

        DynamicResourceLocation ruinedPortal = builtInLootTable(BuiltInLootTables.RUINED_PORTAL);
        LootModifier.builder()
                .addLootTables(ruinedPortal.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.GOLDEN_HATCHET.value(), 0.35))
                .save(consumer, ruinedPortal.fantazia());

        LootModifier.builder()
                .addLootTables(BuiltInLootTables.DESERT_PYRAMID.location(), BuiltInLootTables.JUNGLE_TEMPLE.location())
                .addLootInstance(LootInstance.Builder.of(FTZItems.VITALITY_FRUIT.value(), 1, true))
                .addLootInstance(LootInstance.Builder.of(FTZItems.ATHAME.value(), 0.125))
                .save(consumer, Fantazia.res("temples"));

        DynamicResourceLocation witch = entityLootTable(EntityType.WITCH);
        LootModifier.builder()
                .addLootTables(witch.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.INSIGHT_ESSENCE.value(), 1, true))
                .addLootInstance(LootInstance.Builder.of(FTZItems.ACID_BOTTLE.value(), 0.225))
                .save(consumer, witch.fantazia());

        DynamicResourceLocation wither = entityLootTable(EntityType.WITHER);
        LootModifier.builder()
                .addLootTables(wither.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.WITHERS_QUINTESSENCE.value(), 1, true))
                .save(consumer, wither.fantazia());

        DynamicResourceLocation witherSkeleton = entityLootTable(EntityType.WITHER_SKELETON);
        LootModifier.builder()
                .addLootTables(witherSkeleton.regular())
                .addLootInstance(LootInstance.Builder.of(FTZItems.INSIGHT_ESSENCE.value(), 1, true))
                .save(consumer, witherSkeleton.fantazia());

        DynamicResourceLocation trialChambersCommon = builtInLootTable(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON);
        LootModifier.builder()
                .addLootTables(trialChambersCommon.regular())
                .addLootInstance(LootInstance.Builder.of(RuneWielderItem.rune(Runes.PURE_VESSEL), 0.3))
                .addLootInstance(LootInstance.Builder.of(RuneWielderItem.rune(Runes.OMNIDIRECTIONAL), 0.175))
                .save(consumer, trialChambersCommon.fantazia());

        DynamicResourceLocation phantom = entityLootTable(EntityType.PHANTOM);
        LootModifier.builder()
                .addLootTables(phantom.regular())
                .addLootInstance(LootInstance.Builder.of(RuneWielderItem.rune(Runes.AEROBAT),0.15, Items.PHANTOM_MEMBRANE))
                .save(consumer, phantom.fantazia());
    }

    static DynamicResourceLocation builtInLootTable(ResourceKey<LootTable> key) {
        return new DynamicResourceLocation(key.location());
    }

    static DynamicResourceLocation entityLootTable(EntityType<?> entityType) {
        return new DynamicResourceLocation(entityType.getDefaultLootTable().location());
    }
}
