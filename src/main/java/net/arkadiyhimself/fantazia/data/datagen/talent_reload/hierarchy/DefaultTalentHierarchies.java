package net.arkadiyhimself.fantazia.data.datagen.talent_reload.hierarchy;

import net.arkadiyhimself.fantazia.data.talent.*;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent.DefaultTalents;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Consumer;

public class DefaultTalentHierarchies implements SubProvider<TalentHierarchyHolder> {

    public static DefaultTalentHierarchies create() {
        return new DefaultTalentHierarchies();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<TalentHierarchyHolder> consumer) {
        // abilities
        TalentHierarchyBuilder.builder(TalentTabs.ABILITIES)
                .setHierarchy(ChainHierarchy.builder(Talents.DOUBLE_JUMP)
                        .addElement(Talents.CAT_REFLEXES)
                        .addElement(Talents.FINISHED_WINGS)
                        .build()).save(consumer, TalentHierarchies.AERIAL);

        TalentHierarchyBuilder.builder(TalentTabs.ABILITIES)
                .setHierarchy(ChainHierarchy.builder(Talents.DASH1)
                        .addElement(Talents.DASH2)
                        .addElement(Talents.DASH3)
                        .build()).save(consumer, TalentHierarchies.DASH);

        TalentHierarchyBuilder.builder(TalentTabs.ABILITIES)
                .setHierarchy(ChainHierarchy.builder(Talents.RELENTLESS)
                        .addElement(Talents.SAVAGE)
                        .build()).save(consumer, TalentHierarchies.EUPHORIA);
        
        TalentHierarchyBuilder.builder(TalentTabs.ABILITIES)
                .setHierarchy(ChainHierarchy.builder(Talents.MELEE_BLOCK)
                        .addElement(Talents.PARRY_HAEMORRHAGE)
                        .addElement(Talents.PARRY_DISARM)
                        .build()).save(consumer, TalentHierarchies.MELEE_BLOCK);

        TalentHierarchyBuilder.builder(TalentTabs.ABILITIES)
                .setHierarchy(ChainHierarchy.builder(Talents.WALL_CLIMBING)
                        .addElement(Talents.COBWEB_CLIMBING)
                        .addElement(Talents.POISON_ATTACK)
                        .build()).save(consumer, TalentHierarchies.SPIDER_POWERS);

        // spellcasting
        TalentHierarchyBuilder.builder(TalentTabs.SPELLCASTING)
                .setHierarchy(ChainHierarchy.builder(Talents.MANA_RECYCLE1)
                        .addElement(Talents.MANA_RECYCLE2).addElement(Talents.MANA_RECYCLE3)
                        .build()).save(consumer, TalentHierarchies.MANA_RECYCLE);

        TalentHierarchyBuilder.builder(TalentTabs.SPELLCASTING)
                .setHierarchy(ChaoticHierarchy.of(Talents.ACTIVECASTER_SLOTS, Talents.PASSIVECASTER_SLOTS, Talents.RUNE_SLOTS))
                .save(consumer, TalentHierarchies.SPELLCASTING1);

        TalentHierarchyBuilder.builder(TalentTabs.SPELLCASTING)
                .setHierarchy(ChaoticHierarchy.of(Talents.CAST_RANGE_ADD, Talents.AURA_RANGE_ADD, Talents.SPELL_RECHARGE_REDUCE))
                .save(consumer, TalentHierarchies.SPELLCASTING2);
        
        // stat modifiers
        TalentHierarchyBuilder.builder(TalentTabs.STAT_MODIFIERS)
                .makeSimpleChain(5, simpleChainElement(TalentHierarchies.EVASION_BOOST, false).wisdom(65)
                        .addAttributeModifier(FTZAttributes.EVASION, 5, AttributeModifier.Operation.ADD_VALUE))
                .save(consumer, TalentHierarchies.EVASION_BOOST);

        TalentHierarchyBuilder.builder(TalentTabs.STAT_MODIFIERS)
                .makeSimpleChain(5, simpleChainElement(TalentHierarchies.HEALTH_BOOST, true)
                        .addAttributeModifier(Attributes.MAX_HEALTH, 4, AttributeModifier.Operation.ADD_VALUE)
                        .background(DefaultTalents.RHOMB_PINK)).save(consumer, TalentHierarchies.HEALTH_BOOST);

        TalentHierarchyBuilder.builder(TalentTabs.STAT_MODIFIERS)
                .makeSimpleChain(5, simpleChainElement(TalentHierarchies.MANA_BOOST, true)
                        .addAttributeModifier(FTZAttributes.MAX_MANA, 4, AttributeModifier.Operation.ADD_VALUE)
                        .background(DefaultTalents.RHOMB_BLUE)).save(consumer, TalentHierarchies.MANA_BOOST);
    }

    private static Talent.Builder simpleChainElement(ResourceLocation location, boolean advancement) {
        Talent.Builder builder = Talent.builder();
        builder.icon(location.withPrefix("textures/talent/").withSuffix(".png"));
        builder.title("talent." + location.getNamespace() + "." + location.getPath().replace("/","."));
        if (advancement) builder.advancement(location.withPrefix("talents/"));
        return builder;
    }
}
