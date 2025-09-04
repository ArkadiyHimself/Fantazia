package net.arkadiyhimself.fantazia.data.datagen.advancement;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.criterion.EuphoriaTrigger;
import net.arkadiyhimself.fantazia.data.criterion.ObtainTalentTrigger;
import net.arkadiyhimself.fantazia.data.criterion.UseSpellTrigger;
import net.arkadiyhimself.fantazia.data.talent.Talents;
import net.arkadiyhimself.fantazia.common.item.TheWorldlinessItem;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FantazicAdvancementsRegular implements AdvancementProvider.AdvancementGenerator {

    public static FantazicAdvancementsRegular create() {
        return new FantazicAdvancementsRegular();
    }

    @Override
    public void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        AdvancementHolder rootHolder = Advancement.Builder.advancement()
                .display(FTZItems.OBSCURE_SUBSTANCE,
                        Component.translatable("advancement_tab." + Fantazia.MODID),
                        Component.translatable("advancement_tab." + Fantazia.MODID + ".desc"),
                        ResourceLocation.withDefaultNamespace("textures/block/polished_blackstone_bricks.png"),
                        AdvancementType.TASK, false, false, false)
                .addCriterion("automatic", PlayerTrigger.TriggerInstance.tick()).save(consumer, Fantazia.location("main/root").toString());

        String bloodthirsty = "bloodthirsty";
        AdvancementHolder bloodthirstyHolder = Advancement.Builder.advancement().parent(rootHolder)
                .display(FTZItems.BLOODLUST_AMULET, title(bloodthirsty), desc(bloodthirsty),
                        null, AdvancementType.CHALLENGE, true, true, true)
                .addCriterion("peak_euphoria", EuphoriaTrigger.TriggerInstance.peaking(1200))
                .save(consumer, location(bloodthirsty));

        String caster = "caster";
        AdvancementHolder casterHolder = Advancement.Builder.advancement().parent(rootHolder)
                .display(FTZItems.ENTANGLER, title(caster), desc(caster),
                        null, AdvancementType.TASK, true, false, false)
                .addCriterion("activecaster", InventoryChangeTrigger.TriggerInstance.hasItems(FantazicUtil.itemTagPredicate(FTZItemTags.CURIOS_ACTIVECASTER)))
                .addCriterion("passivecaster", InventoryChangeTrigger.TriggerInstance.hasItems(FantazicUtil.itemTagPredicate(FTZItemTags.CURIOS_PASSIVECASTER)))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(consumer, location(caster));

        String athame = FTZItems.ATHAME.getId().getPath();
        AdvancementHolder athameHolder = Advancement.Builder.advancement().parent(casterHolder)
                .display(FTZItems.ATHAME, title(athame), desc(athame),
                        null, AdvancementType.TASK, false, false, false)
                .addCriterion(athame, InventoryChangeTrigger.TriggerInstance.hasItems(FTZItems.ATHAME))
                .save(consumer, location(athame));

        String cardDeck = FTZItems.CARD_DECK.getId().getPath();
        AdvancementHolder cardDeckHolder = Advancement.Builder.advancement().parent(casterHolder)
                .display(FTZItems.CARD_DECK, title(cardDeck), desc(cardDeck),
                        null, AdvancementType.TASK, true, false, false)
                .addCriterion(cardDeck, InventoryChangeTrigger.TriggerInstance.hasItems(FTZItems.CARD_DECK))
                .save(consumer, location(cardDeck));

        String caughtThunder = FTZItems.CAUGHT_THUNDER.getId().getPath();
        AdvancementHolder caughtThunderHolder = Advancement.Builder.advancement().parent(casterHolder)
                .display(FTZItems.CAUGHT_THUNDER, title(caughtThunder), desc(caughtThunder),
                        null, AdvancementType.TASK, true, false, false)
                .addCriterion(caughtThunder, InventoryChangeTrigger.TriggerInstance.hasItems(FTZItems.CAUGHT_THUNDER))
                .save(consumer, location(caughtThunder));


        String dashstone1 = FTZItems.DASHSTONE.getId().getPath() + "1";
        AdvancementHolder dashstone1Holder = Advancement.Builder.advancement().parent(rootHolder)
                .display(FantazicUtil.dashStone(1), title(dashstone1), desc(dashstone1),
                        null, AdvancementType.GOAL, true, true, true)
                .addCriterion("equip", FantazicUtil.equipCurioTrigger(FTZDataComponentTypes.DASH_LEVEL.value(), 1, "dashstone"))
                .save(consumer, location(dashstone1));

        String dashstone2 = FTZItems.DASHSTONE.getId().getPath() + "2";
        AdvancementHolder dashstone2Holder = Advancement.Builder.advancement().parent(dashstone1Holder)
                .display(FantazicUtil.dashStone(2), title(dashstone2), desc(dashstone2),
                        null, AdvancementType.GOAL, true, true, true)
                .addCriterion("equip", FantazicUtil.equipCurioTrigger(FTZDataComponentTypes.DASH_LEVEL.value(), 2, "dashstone"))
                .save(consumer, location(dashstone2));

        String dashstone3 = FTZItems.DASHSTONE.getId().getPath() + "3";
        AdvancementHolder dashstone3Holder = Advancement.Builder.advancement().parent(dashstone2Holder)
                .display(FantazicUtil.dashStone(3), title(dashstone3), desc(dashstone3),
                        null, AdvancementType.GOAL, true, true, true)
                .addCriterion("equip", FantazicUtil.equipCurioTrigger(FTZDataComponentTypes.DASH_LEVEL.value(), 3, "dashstone"))
                .save(consumer, location(dashstone3));

        String doomed = FTZMobEffects.DOOMED.getId().getPath();
        AdvancementHolder doomedHolder = Advancement.Builder.advancement().parent(rootHolder)
                .display(FTZItems.SOUL_EATER, title(doomed), desc(doomed),
                        null, AdvancementType.TASK, true, true, true)
                .addCriterion(doomed, changedEffect(FTZMobEffects.DOOMED))
                .save(consumer, location(doomed));

        String obtainTalent = "obtain_a_talent";
        AdvancementHolder obtainTalentHolder = Advancement.Builder.advancement().parent(rootHolder)
                .display(Items.IRON_INGOT, title(obtainTalent), desc(obtainTalent),
                        null, AdvancementType.GOAL, true, false, false)
                .addCriterion(obtainTalent, ObtainTalentTrigger.TriggerInstance.any())
                .save(consumer, location(obtainTalent));

        String obtain15talents = "obtain_15_talents";
        AdvancementHolder obtain15talentsHolder = Advancement.Builder.advancement().parent(obtainTalentHolder)
                .display(Items.DIAMOND, title(obtain15talents), desc(obtain15talents),
                        null, AdvancementType.GOAL, true, false, true)
                .addCriterion(obtain15talents, ObtainTalentTrigger.TriggerInstance.amount(15))
                .save(consumer, location(obtain15talents));

        String obtain30talents = "obtain_30_talents";
        AdvancementHolder obtain30talentsHolder = Advancement.Builder.advancement().parent(obtain15talentsHolder)
                .display(Items.NETHER_STAR, title(obtain30talents), desc(obtain30talents),
                        null, AdvancementType.CHALLENGE, true, false, true)
                .addCriterion(obtain30talents, ObtainTalentTrigger.TriggerInstance.amount(30))
                .save(consumer, location(obtain30talents));

        String obtainSpellRechargeReduce = "obtain_spell_recharge_reduce";
        AdvancementHolder obtainSpellRechargeReduceHolder = Advancement.Builder.advancement().parent(obtainTalentHolder)
                .display(FTZItems.ENIGMATIC_CLOCK, title(obtainSpellRechargeReduce), desc(obtainSpellRechargeReduce),
                        null, AdvancementType.GOAL, true, true, true)
                .addCriterion(obtainSpellRechargeReduce, ObtainTalentTrigger.TriggerInstance.specific(Talents.SPELL_RECHARGE_REDUCE))
                .save(consumer, location(obtainSpellRechargeReduce));

        String spiralNemesis = FTZItems.SPIRAL_NEMESIS.getId().getPath();
        AdvancementHolder spiralNemesisHolder = Advancement.Builder.advancement().parent(casterHolder)
                .display(FTZItems.SPIRAL_NEMESIS, title(spiralNemesis), desc(spiralNemesis),
                        null, AdvancementType.TASK, true, true, true)
                .addCriterion(spiralNemesis, InventoryChangeTrigger.TriggerInstance.hasItems(FTZItems.SPIRAL_NEMESIS))
                .save(consumer, location(spiralNemesis));

        String theWorldliness = "the_worldliness";
        AdvancementHolder theWorldlinessHolder = Advancement.Builder.advancement().parent(rootHolder)
                .display(TheWorldlinessItem.itemStack(), title(theWorldliness), desc(theWorldliness),
                        null, AdvancementType.TASK, true, false, false)
                .addCriterion(theWorldliness, theWorldlinessPredicate())
                .save(consumer, location(theWorldliness));

        String netheriteHatchet = "netherite_hatchet";
        AdvancementHolder netheriteHatchetHolder = Advancement.Builder.advancement().parent(rootHolder)
                .display(FTZItems.NETHERITE_HATCHET.value().getDefaultInstance(), title(netheriteHatchet), desc(netheriteHatchet),
                        null, AdvancementType.GOAL, true, true, false)
                .addCriterion(netheriteHatchet, InventoryChangeTrigger.TriggerInstance.hasItems(FTZItems.NETHERITE_HATCHET))
                .save(consumer, location(netheriteHatchet));

        String wandererTraderTeleport = "wanderer_trader_teleport";
        AdvancementHolder wandererTraderTeleportHolder = Advancement.Builder.advancement().parent(obtainSpellRechargeReduceHolder)
                .display(FTZItems.ROAMERS_COMPASS.value().getDefaultInstance(), title(wandererTraderTeleport), desc(wandererTraderTeleport),
                        null, AdvancementType.CHALLENGE, true, true,true)
                .addCriterion(wandererTraderTeleport, UseSpellTrigger.INSTANCE.createCriterion(
                        UseSpellTrigger.TriggerInstance.useSpellOn(
                                Spells.WANDERERS_SPIRIT,
                                EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityType.WANDERING_TRADER)).build()
                        )
                ))
                .save(consumer, location(wandererTraderTeleport));
    }

    private static String location(String name) {
        return Fantazia.location("main/" + name).toString();
    }

    private static Component title(String name) {
        return Component.translatable("advancement." + Fantazia.MODID + "." + name);
    }

    private static Component desc(String name) {
        return Component.translatable("advancement." + Fantazia.MODID + "." + name + ".desc");
    }

    private static Criterion<EffectsChangedTrigger.TriggerInstance> changedEffect(Holder<MobEffect> holder) {
        return EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(holder));
    }

    private static <T> Criterion<InventoryChangeTrigger.TriggerInstance> theWorldlinessPredicate() {
        ItemStack stack = TheWorldlinessItem.itemStack();
        ItemPredicate.Builder item = ItemPredicate.Builder.item();
        DataComponentPredicate.Builder dataComponent = DataComponentPredicate.builder();
        item.hasComponents(dataComponent.build());
        DataComponentType<T> book = null;
        try {
            book = (DataComponentType<T>) BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse("patchouli:book"));
        } catch (Exception ignored) {}

        if (book == null) return InventoryChangeTrigger.TriggerInstance.hasItems(item.build());
        T object = stack.get(book);
        if (object instanceof ResourceLocation) dataComponent.expect(book, object);

        return InventoryChangeTrigger.TriggerInstance.hasItems(item.hasComponents(dataComponent.build()));
    }
}
