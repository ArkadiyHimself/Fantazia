package net.arkadiyhimself.fantazia.datagen.talent_reload.hierarchy;

import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.arkadiyhimself.fantazia.datagen.talent_reload.talent.DefaultTalents;
import net.arkadiyhimself.fantazia.data.talent.*;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.awt.*;
import java.util.function.Consumer;

public class DefaultTalentHierarchies implements SubProvider<TalentHierarchyHolder> {

    public static DefaultTalentHierarchies create() {
        return new DefaultTalentHierarchies();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<TalentHierarchyHolder> consumer) {
        // abilities
        abilitiesTab()
                .setHierarchy(ChainHierarchy.builder(Talents.DOUBLE_JUMP)
                        .addElement(Talents.CAT_REFLEXES)
                        .addElement(Talents.FINISHED_WINGS)
                        .build()).save(consumer, TalentHierarchies.AERIAL);

        abilitiesTab()
                .setHierarchy(ChainHierarchy.builder(Talents.DASH1)
                        .addElement(Talents.DASH2)
                        .addElement(Talents.DASH3)
                        .build()).save(consumer, TalentHierarchies.DASH);

        abilitiesTab()
                .setHierarchy(ChainHierarchy.builder(Talents.RELENTLESS)
                        .addElement(Talents.SAVAGE)
                        .build()).save(consumer, TalentHierarchies.EUPHORIA);
        
        abilitiesTab()
                .setHierarchy(ChainHierarchy.builder(Talents.MELEE_BLOCK)
                        .addElement(Talents.PARRY_HAEMORRHAGE)
                        .addElement(Talents.PARRY_DISARM)
                        .build()).save(consumer, TalentHierarchies.MELEE_BLOCK);

        abilitiesTab()
                .setHierarchy(ChainHierarchy.builder(Talents.WALL_CLIMBING)
                        .addElement(Talents.COBWEB_CLIMBING)
                        .addElement(Talents.POISON_ATTACK)
                        .build()).save(consumer, TalentHierarchies.SPIDER_POWERS);

        // spellcasting
        spellCastingTab()
                .setHierarchy(ChainHierarchy.builder(Talents.MANA_RECYCLE1)
                        .addElement(Talents.MANA_RECYCLE2).addElement(Talents.MANA_RECYCLE3)
                        .build()).save(consumer, TalentHierarchies.MANA_RECYCLE);

        spellCastingTab()
                .setHierarchy(
                        ChaoticHierarchy.of(
                                Talents.ACTIVECASTER_SLOTS,
                                Talents.PASSIVECASTER_SLOTS,
                                Talents.RUNE_SLOTS
                        )
                )
                .noLine()
                .save(consumer, TalentHierarchies.SPELLCASTING1);

        spellCastingTab()
                .setHierarchy(
                        ChaoticHierarchy.of(
                                Talents.CAST_RANGE_ADD,
                                Talents.AURA_RANGE_ADD,
                                Talents.SPELL_RECHARGE_REDUCE
                        )
                )
                .noLine()
                .save(consumer, TalentHierarchies.SPELLCASTING2);
        
        // stat modifiers
        statModifiersTab()
                .lineColors(new Color(126,131,0).getRGB(), new Color(148,153,17).getRGB())
                .makeSimpleChain(5, simpleChainElement(TalentHierarchies.EVASION_BOOST, false).wisdom(65)
                        .addAttributeModifier(FTZAttributes.EVASION, 5, AttributeModifier.Operation.ADD_VALUE))
                .save(consumer, TalentHierarchies.EVASION_BOOST);

        statModifiersTab()
                .lineColors(new Color(12,64,197).getRGB(), new Color(37,98,223).getRGB())
                .makeSimpleChain(5, simpleChainElement(TalentHierarchies.HEALTH_BOOST, true)
                        .addAttributeModifier(Attributes.MAX_HEALTH, 4, AttributeModifier.Operation.ADD_VALUE)
                        .background(DefaultTalents.FANCY_SQUARE_BLUE)).save(consumer, TalentHierarchies.HEALTH_BOOST);

        statModifiersTab()
                .lineColors(new Color(194,126,0).getRGB(), new Color(220,167,26).getRGB())
                .makeSimpleChain(5, simpleChainElement(TalentHierarchies.MANA_BOOST, true)
                        .addAttributeModifier(FTZAttributes.MAX_MANA, 4, AttributeModifier.Operation.ADD_VALUE)
                        .background(DefaultTalents.FANCY_SQUARE_YELLOW)).save(consumer, TalentHierarchies.MANA_BOOST);

        statModifiersTab()
                .lineColors(new Color(93,3,3).getRGB(), new Color(134,10,10).getRGB())
                .setHierarchy(ChaoticHierarchy.of(
                        Talents.NETHERITE_HELMET_REWARD,
                        Talents.NETHERITE_CHESTPLATE_REWARD,
                        Talents.NETHERITE_LEGGINGS_REWARD,
                        Talents.NETHERITE_BOOTS_REWARD,
                        Talents.NETHERITE_SWORD_REWARD,
                        Talents.NETHERITE_SHOVEL_REWARD,
                        Talents.NETHERITE_PICKAXE_REWARD,
                        Talents.NETHERITE_AXE_REWARD,
                        Talents.NETHERITE_HOE_REWARD,
                        Talents.NETHERITE_HATCHET_REWARD
                )).save(consumer, TalentHierarchies.NETHERITE_EQUIPMENT_REWARD);

        // engineering
        engineeringTab()
                .setHierarchy(
                        ChaoticHierarchy.of(
                                Talents.TOOL_CAPACITY_UPGRADE1,
                                Talents.TOOL_CAPACITY_UPGRADE2,
                                Talents.TOOL_CAPACITY_UPGRADE3,
                                Talents.TOOL_CAPACITY_UPGRADE4
                        )
                ).save(consumer, TalentHierarchies.CAPACITY_UPGRADE);
        engineeringTab()
                .setHierarchy(
                        ChaoticHierarchy.of(
                                Talents.TOOL_DAMAGE_UPGRADE1,
                                Talents.TOOL_DAMAGE_UPGRADE2,
                                Talents.TOOL_DAMAGE_UPGRADE3,
                                Talents.TOOL_DAMAGE_UPGRADE4
                        )
                ).save(consumer, TalentHierarchies.DAMAGE_UPGRADE);
    }

    private static TalentIdHierarchy.Builder abilitiesTab() {
        return TalentIdHierarchy.Builder.builder(TalentTabs.ABILITIES);
    }

    private static TalentIdHierarchy.Builder spellCastingTab() {
        return TalentIdHierarchy.Builder.builder(TalentTabs.SPELLCASTING);
    }

    private static TalentIdHierarchy.Builder statModifiersTab() {
        return TalentIdHierarchy.Builder.builder(TalentTabs.STAT_MODIFIERS);
    }

    private static TalentIdHierarchy.Builder engineeringTab() {
        return TalentIdHierarchy.Builder.builder(TalentTabs.ENGINEERING);
    }

    private static Talent.Builder simpleChainElement(ResourceLocation location, boolean advancement) {
        Talent.Builder builder = Talent.builder();
        builder.icon(location.withPrefix("textures/talent/").withSuffix(".png"));
        builder.title("talent." + location.getNamespace() + "." + location.getPath().replace("/","."));
        if (advancement) builder.advancement(location.withPrefix("talents/"));
        return builder;
    }
}
