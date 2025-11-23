package net.arkadiyhimself.fantazia.data.datagen.advancement.the_worldliness;

import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.criterion.PossessItemTrigger;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ArtifactCategoryAdvancements {

    public static void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        artifactKilled(consumer, FTZItems.ACID_BOTTLE, EntityType.WITCH);
        artifactBiomes(consumer, FTZItems.AMPLIFIED_ICE, provider, Tags.Biomes.IS_COLD_OVERWORLD);
        artifactStructures(consumer, FTZItems.ATHAME, provider, BuiltinStructures.DESERT_PYRAMID, BuiltinStructures.JUNGLE_TEMPLE);
        artifactDimension(consumer, FTZItems.BLOODLUST_AMULET, Level.NETHER);
        artifactStructures(consumer, FTZItems.BROKEN_STAFF, provider, StructureTags.VILLAGE);
        artifactCraftableObscureSubstance(consumer, FTZItems.CARD_DECK, Items.PAPER);
        artifactStructures(consumer, FTZItems.CAUGHT_THUNDER, provider, BuiltinStructures.SHIPWRECK);
        artifactCraftableObscureSubstance(consumer, FTZItems.CONTAINED_SOUND, Items.SCULK_SHRIEKER);
        artifactFound(consumer, FTZItems.DASHSTONE);
        artifactCraftableObscureSubstance(consumer, FTZItems.ENIGMATIC_CLOCK, Items.CLOCK);
        artifactCraftableObscureSubstance(consumer, FTZItems.ENTANGLER, Items.TOTEM_OF_UNDYING);
        artifactStructures(consumer, FTZItems.HEART_OF_SCULK, provider, BuiltinStructures.ANCIENT_CITY);
        artifactStructures(consumer, FTZItems.LEADERS_HORN, provider, BuiltinStructures.PILLAGER_OUTPOST);
        artifactCraftableObscureSubstance(consumer, FTZItems.MYSTIC_MIRROR, Items.GLASS);
        artifactCraftableFantaziumIngot(consumer, FTZItems.NECKLACE_OF_CLAIRVOYANCE);
        artifactKilled(consumer, FTZItems.NETHER_HEART, EntityType.BLAZE);
        artifactCraftableObscureSubstance(consumer, FTZItems.NIMBLE_DAGGER, Items.ENDER_PEARL);
        artifactCraftableObscureSubstance(consumer, FTZItems.OPTICAL_LENS, Items.GLASS_PANE);
        artifactCraftableObscureSubstance(consumer, FTZItems.PUPPET_DOLL, Items.ARMOR_STAND);
        artifactCraftableObscureSubstance(consumer, FTZItems.ROAMERS_COMPASS, Items.REDSTONE);
        artifactCraftableObscureSubstance(consumer, FTZItems.RUSTY_RING, Items.IRON_NUGGET);
        artifactCraftableObscureSubstance(consumer, FTZItems.SANDMANS_DUST, Items.SAND);
        artifactDimension(consumer, FTZItems.SOUL_EATER, Level.NETHER);
        artifactKilled(consumer, FTZItems.SPIRAL_NEMESIS, EntityType.ENDERMAN);
        artifactStructures(consumer, FTZItems.TRANQUIL_HERB, provider, BuiltinStructures.MINESHAFT);
        artifactCraftableFantaziumIngot(consumer, FTZItems.WISDOM_CATCHER, Items.GOLD_INGOT);
        artifactKilled(consumer, FTZItems.WITHERS_QUINTESSENCE, EntityType.WITHER_SKELETON);
        artifactCraftableObscureSubstance(consumer, FTZItems.OMINOUS_BELL, Items.BELL);
    }

    private static void artifactCraftableFantaziumIngot(Consumer<AdvancementHolder> consumer, ItemLike artifact, ItemLike... ingredients) {
        EntryAdvancementHolder holder = artifact();
        List<List<String>> requirements = Lists.newArrayList();
        String artifactCriteria = "found_" + BuiltInRegistries.ITEM.getKey(artifact.asItem()).getPath();
        String fantaziumCriteria = FTZItems.FANTAZIUM_INGOT.getId().getPath();
        holder.addCriterion(artifactCriteria, InventoryChangeTrigger.TriggerInstance.hasItems(artifact));
        holder.addCriterion(fantaziumCriteria, InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(FTZItemTags.FROM_FANTAZIUM)));
        for (ItemLike item : ingredients) {
            String name = BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
            Criterion<InventoryChangeTrigger.TriggerInstance> criterion = InventoryChangeTrigger.TriggerInstance.hasItems(item);
            holder.addCriterion(name, criterion);
            requirements.add(List.of(name, artifactCriteria));
        }
        requirements.add(List.of(artifactCriteria, fantaziumCriteria));
        holder.requirements(new AdvancementRequirements(requirements));
        holder.save(consumer, artifact);
    }

    private static void artifactCraftableObscureSubstance(Consumer<AdvancementHolder> consumer, ItemLike artifact, ItemLike... ingredients) {
        EntryAdvancementHolder holder = artifact();
        List<List<String>> requirements = Lists.newArrayList();
        String artifactCriteria = "found_" + BuiltInRegistries.ITEM.getKey(artifact.asItem()).getPath();
        String substanceCriteria = FTZItems.OBSCURE_SUBSTANCE.getId().getPath();
        holder.addCriterion(artifactCriteria, InventoryChangeTrigger.TriggerInstance.hasItems(artifact));
        holder.addCriterion(substanceCriteria, PossessItemTrigger.TriggerInstance.specificItems(FTZItems.OBSCURE_SUBSTANCE));
        for (ItemLike item : ingredients) {
            String name = BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
            Criterion<InventoryChangeTrigger.TriggerInstance> criterion = InventoryChangeTrigger.TriggerInstance.hasItems(item);
            holder.addCriterion(name, criterion);
            requirements.add(List.of(name, artifactCriteria));
        }
        requirements.add(List.of(artifactCriteria, substanceCriteria));
        holder.requirements(new AdvancementRequirements(requirements));
        holder.save(consumer, artifact);
    }

    private static void artifactFound(Consumer<AdvancementHolder> consumer, ItemLike artifact) {
        EntryAdvancementHolder holder = artifact();
        holder.addCriterion("found_" + BuiltInRegistries.ITEM.getKey(artifact.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(artifact));
        holder.save(consumer, artifact);
    }

    private static void artifactDimension(Consumer<AdvancementHolder> consumer, ItemLike artifact, ResourceKey<Level> key) {
        EntryAdvancementHolder holder = artifact();
        holder.addCriterion("found_" + BuiltInRegistries.ITEM.getKey(artifact.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(artifact));
        holder.addCriterion("entered_" + key.location().getPath(), ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(key));
        holder.requirements(AdvancementRequirements.Strategy.OR);
        holder.save(consumer, artifact);
    }

    private static void artifactKilled(Consumer<AdvancementHolder> consumer, ItemLike artifact, EntityType<?> entityType) {
        EntryAdvancementHolder holder = artifact();
        holder.addCriterion("found_" + BuiltInRegistries.ITEM.getKey(artifact.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(artifact));
        holder.addCriterion("killed_" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath(), KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityType)));
        holder.requirements(AdvancementRequirements.Strategy.OR);
        holder.save(consumer, artifact);
    }

    @SafeVarargs
    private static void artifactStructures(Consumer<AdvancementHolder> consumer, ItemLike artifact, HolderLookup.Provider provider, ResourceKey<Structure>... structures) {
        EntryAdvancementHolder holder = artifact().requirements(AdvancementRequirements.Strategy.OR);
        holder.addCriterion("found_" + BuiltInRegistries.ITEM.getKey(artifact.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(artifact));
        for (ResourceKey<Structure> key : structures) {
            LocationPredicate.Builder predicate = LocationPredicate.Builder.inStructure(provider.holderOrThrow(key));
            holder.addCriterion("visited_" + key.location().getPath(), PlayerTrigger.TriggerInstance.located(predicate));
        }
        holder.requirements(AdvancementRequirements.Strategy.OR);
        holder.save(consumer, artifact);
    }

    private static void artifactBiomes(Consumer<AdvancementHolder> consumer, ItemLike artifact, HolderLookup.Provider provider, TagKey<Biome> tagKey) {
        LocationPredicate.Builder builder = new LocationPredicate.Builder().setBiomes(provider.lookupOrThrow(Registries.BIOME).get(tagKey).get());
        artifactLocation(consumer, artifact, tagKey.location().getPath(), builder);
    }

    private static void artifactStructures(Consumer<AdvancementHolder> consumer, ItemLike artifact, HolderLookup.Provider provider, TagKey<Structure> tagKey) {
        LocationPredicate.Builder builder = new LocationPredicate.Builder().setStructures(provider.lookupOrThrow(Registries.STRUCTURE).get(tagKey).get());
        artifactLocation(consumer, artifact, tagKey.location().getPath(), builder);
    }

    private static void artifactLocation(Consumer<AdvancementHolder> consumer, ItemLike artifact, String name, LocationPredicate.Builder predicate) {
        EntryAdvancementHolder holder = artifact();
        holder.addCriterion("found_" + BuiltInRegistries.ITEM.getKey(artifact.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(artifact));
        holder.addCriterion(name, PlayerTrigger.TriggerInstance.located(predicate));
        holder.requirements(AdvancementRequirements.Strategy.OR);
        holder.save(consumer, artifact);
    }

    private static void artifactSave(Consumer<AdvancementHolder> consumer, ItemLike artifact, String key, Criterion<?> criterion) {
        artifact().addCriterion(key, criterion).save(consumer, artifact);
    }

    private static EntryAdvancementHolder artifact() {
        return new EntryAdvancementHolder("artifacts");
    }
}
