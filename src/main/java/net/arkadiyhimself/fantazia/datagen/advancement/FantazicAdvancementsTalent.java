package net.arkadiyhimself.fantazia.datagen.advancement;

import net.arkadiyhimself.fantazia.data.criterion.EuphoriaTrigger;
import net.arkadiyhimself.fantazia.data.criterion.MeleeBlockTrigger;
import net.arkadiyhimself.fantazia.data.criterion.PossessItemTrigger;
import net.arkadiyhimself.fantazia.data.criterion.PossessRuneTrigger;
import net.arkadiyhimself.fantazia.data.talent.TalentHierarchies;
import net.arkadiyhimself.fantazia.data.talent.Talents;
import net.arkadiyhimself.fantazia.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FantazicAdvancementsTalent implements AdvancementProvider.AdvancementGenerator {

    public static FantazicAdvancementsTalent create() {
        return new FantazicAdvancementsTalent();
    }

    @Override
    public void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        talent().addCriterion("wings", ConsumeItemTrigger.TriggerInstance.usedItem(FTZItems.UNFINISHED_WINGS)).save(consumer, Talents.DOUBLE_JUMP);
        talent().addCriterion("elytra", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ELYTRA)).save(consumer, Talents.FINISHED_WINGS);

        talent().addCriterion("dashstone", FantazicUtil.equipCurioTrigger(FTZDataComponentTypes.DASH_LEVEL.value(), 1, "dashstone")).save(consumer, Talents.DASH1);
        talent().addCriterion("dashstone", FantazicUtil.equipCurioTrigger(FTZDataComponentTypes.DASH_LEVEL.value(), 2, "dashstone")).save(consumer, Talents.DASH2);
        talent().addCriterion("dashstone", FantazicUtil.equipCurioTrigger(FTZDataComponentTypes.DASH_LEVEL.value(), 3, "dashstone")).save(consumer, Talents.DASH3);

        talent().addCriterion("reached_peak", EuphoriaTrigger.TriggerInstance.combo(10)).save(consumer, Talents.RELENTLESS);

        talent().addCriterion("ate_eye", ConsumeItemTrigger.TriggerInstance.usedItem(FTZItems.ARACHNID_EYE)).save(consumer, Talents.WALL_CLIMBING);

        talent().addCriterion("right_weapon", InventoryChangeTrigger.TriggerInstance.hasItems(FantazicUtil.itemTagPredicate(FTZItemTags.MELEE_BLOCK))).save(consumer, Talents.MELEE_BLOCK);
        talent().addCriterion("ten_times", MeleeBlockTrigger.TriggerInstance.parriesCriterion(10)).save(consumer, Talents.PARRY_HAEMORRHAGE);
        talent().addCriterion("twenty_times", MeleeBlockTrigger.TriggerInstance.parriesCriterion(20)).save(consumer, Talents.PARRY_DISARM);

        talent().addCriterion("enough_casters", PossessItemTrigger.TriggerInstance.itemsOfTag(FTZItemTags.CURIOS_PASSIVECASTER, 4)).save(consumer, Talents.PASSIVECASTER_SLOTS);
        talent().addCriterion("enough_casters", PossessItemTrigger.TriggerInstance.itemsOfTag(FTZItemTags.CURIOS_ACTIVECASTER, 4)).save(consumer, Talents.ACTIVECASTER_SLOTS);
        talent().addCriterion("enough_runes", PossessRuneTrigger.TriggerInstance.amountOfRunes(6)).save(consumer, Talents.RUNE_SLOTS);

        talent().addCriterion("ate_fruit", ConsumeItemTrigger.TriggerInstance.usedItem(FTZItems.VITALITY_FRUIT)).save(consumer, TalentHierarchies.HEALTH_BOOST);
        talent().addCriterion("ate_fruit", ConsumeItemTrigger.TriggerInstance.usedItem(FTZItems.INSIGHT_ESSENCE)).save(consumer, TalentHierarchies.MANA_BOOST);
    }

    private static TalentAdvancementHolder talent() {
        return new TalentAdvancementHolder();
    }

    private static class TalentAdvancementHolder {

        private final Advancement.Builder builder = new Advancement.Builder();

        private TalentAdvancementHolder addCriterion(String key, Criterion<?> criterion) {
            builder.addCriterion(key, criterion);
            return this;
        }

        private void save(Consumer<AdvancementHolder> consumer, ResourceLocation location) {
            builder.save(consumer, location.withPrefix("talents/").toString());
        }
    }
}
