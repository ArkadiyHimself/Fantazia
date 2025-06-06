package net.arkadiyhimself.fantazia.datagen.talent_reload.talent;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.curio.FTZSlots;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.talent.TalentImpacts;
import net.arkadiyhimself.fantazia.data.talent.Talents;
import net.arkadiyhimself.fantazia.datagen.FantazicCurioProvider;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.function.Consumer;

public class DefaultTalents implements SubProvider<TalentBuilderHolder> {
    
    public static DefaultTalents create() {
        return new DefaultTalents();
    }

    public static final ResourceLocation CIRCLE_BLUE = Fantazia.res("textures/gui/talent_icons/circle_blue.png");
    public static final ResourceLocation CIRCLE_LIGHT_BLUE = Fantazia.res("textures/gui/talent_icons/circle_light_blue.png");
    public static final ResourceLocation OCTAGON_WHITE = Fantazia.res("textures/gui/talent_icons/octagon_white.png");
    public static final ResourceLocation OCTAGON_BLUE = Fantazia.res("textures/gui/talent_icons/octagon_blue.png");
    public static final ResourceLocation OCTAGON_PURPLE = Fantazia.res("textures/gui/talent_icons/octagon_purple.png");
    public static final ResourceLocation SQUARE_WHITE = Fantazia.res("textures/gui/talent_icons/square_white.png");
    public static final ResourceLocation SQUARE_PURPLE = Fantazia.res("textures/gui/talent_icons/square_purple.png");
    public static final ResourceLocation SQUARE_GREEN = Fantazia.res("textures/gui/talent_icons/square_green.png");
    public static final ResourceLocation SQUARE_RED = Fantazia.res("textures/gui/talent_icons/square_red.png");
    public static final ResourceLocation RHOMB_BLUE = Fantazia.res("textures/gui/talent_icons/rhomb_blue.png");
    public static final ResourceLocation RHOMB_PINK = Fantazia.res("textures/gui/talent_icons/rhomb_pink.png");

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<TalentBuilderHolder> consumer) {
        createBuilder(Talents.DOUBLE_JUMP,true).addImpact(TalentImpacts.DOUBLE_JUMP_UNLOCK).background(SQUARE_WHITE).save(consumer, Talents.DOUBLE_JUMP);
        createBuilder(Talents.CAT_REFLEXES,false).background(SQUARE_WHITE)
                .addDamageMultiplier(0.5f, DamageTypes.FALL)
                .wisdom(50).save(consumer, Talents.CAT_REFLEXES);
        createBuilder(Talents.FINISHED_WINGS, true).addImpact(TalentImpacts.DOUBLE_JUMP_ELYTRA).background(SQUARE_WHITE).save(consumer, Talents.FINISHED_WINGS);

        createBuilder(Talents.DASH1,true).addImpact(TalentImpacts.DASH_UPGRADE).background(OCTAGON_WHITE).save(consumer, Talents.DASH1);
        createBuilder(Talents.DASH2,true).addImpact(TalentImpacts.DASH_UPGRADE).background(OCTAGON_BLUE).save(consumer, Talents.DASH2);
        createBuilder(Talents.DASH3,true).addImpact(TalentImpacts.DASH_UPGRADE)
                .addDamageImmunities(DamageTypes.IN_WALL).background(OCTAGON_PURPLE).save(consumer, Talents.DASH3);

        createBuilder(Talents.RELENTLESS,true).addImpact(TalentImpacts.EUPHORIA_RELENTLESS).background(SQUARE_RED).save(consumer, Talents.RELENTLESS);
        createBuilder(Talents.SAVAGE,false).addImpact(TalentImpacts.EUPHORIA_SAVAGE).wisdom(30).background(SQUARE_RED).save(consumer, Talents.SAVAGE);

        createBuilder(Talents.MANA_RECYCLE1,false).addImpact(TalentImpacts.MANA_RECYCLE_UPGRADE).wisdom(30).background(CIRCLE_LIGHT_BLUE).save(consumer, Talents.MANA_RECYCLE1);
        createBuilder(Talents.MANA_RECYCLE2,false).addImpact(TalentImpacts.MANA_RECYCLE_UPGRADE).wisdom(30).background(CIRCLE_LIGHT_BLUE).save(consumer, Talents.MANA_RECYCLE2);
        createBuilder(Talents.MANA_RECYCLE3,false).addImpact(TalentImpacts.MANA_RECYCLE_UPGRADE).wisdom(30).background(CIRCLE_LIGHT_BLUE).save(consumer, Talents.MANA_RECYCLE3);

        createBuilder(Talents.MELEE_BLOCK,true).addImpact(TalentImpacts.MELEE_BLOCK_UNLOCK).save(consumer, Talents.MELEE_BLOCK);
        createBuilder(Talents.PARRY_HAEMORRHAGE,true).addImpact(TalentImpacts.MELEE_BLOCK_BLOODLOSS).save(consumer, Talents.PARRY_HAEMORRHAGE);
        createBuilder(Talents.PARRY_DISARM,true).addImpact(TalentImpacts.MELEE_BLOCK_DISARM).save(consumer, Talents.PARRY_DISARM);
        
        createBuilder(Talents.PASSIVECASTER_SLOTS,true).background(SQUARE_PURPLE)
                .addCurioModifiers(FTZSlots.PASSIVECASTER,1)
                .save(consumer, Talents.PASSIVECASTER_SLOTS);
        createBuilder(Talents.ACTIVECASTER_SLOTS, true).background(SQUARE_PURPLE)
                .addCurioModifiers(FTZSlots.ACTIVECASTER, 1)
                .save(consumer, Talents.ACTIVECASTER_SLOTS);
        createBuilder(Talents.RUNE_SLOTS, true).background(SQUARE_PURPLE)
                .addCurioModifiers(FTZSlots.RUNE, 1)
                .save(consumer, Talents.RUNE_SLOTS);

        createBuilder(Talents.AURA_RANGE_ADD,false)
                .addAttributeModifier(FTZAttributes.AURA_RANGE_ADDITION, 4.5, AttributeModifier.Operation.ADD_VALUE)
                .wisdom(30).save(consumer, Talents.AURA_RANGE_ADD);
        createBuilder(Talents.CAST_RANGE_ADD,false)
                .addAttributeModifier(FTZAttributes.CAST_RANGE_ADDITION, 4, AttributeModifier.Operation.ADD_VALUE)
                .wisdom(50).save(consumer, Talents.CAST_RANGE_ADD);
        createBuilder(Talents.SPELL_RECHARGE_REDUCE, false)
                .addAttributeModifier(FTZAttributes.RECHARGE_MULTIPLIER,  -20, AttributeModifier.Operation.ADD_VALUE)
                .wisdom(80).save(consumer, Talents.SPELL_RECHARGE_REDUCE);

        createBuilder(Talents.WALL_CLIMBING,true).addImpact(TalentImpacts.WALL_CLIMBING_UNLOCKED)
                .background(SQUARE_GREEN).save(consumer, Talents.WALL_CLIMBING);
        createBuilder(Talents.COBWEB_CLIMBING,false).addImpact(TalentImpacts.WALL_CLIMBING_COBWEB)
                .background(SQUARE_GREEN).wisdom(25).save(consumer, Talents.COBWEB_CLIMBING);
        createBuilder(Talents.POISON_ATTACK,false).addImpact(TalentImpacts.WALL_CLIMBING_POISON)
                .addDamageMultiplier(0.5f, NeoForgeMod.POISON_DAMAGE)
                .background(SQUARE_GREEN).wisdom(50).save(consumer, Talents.POISON_ATTACK);
    }
    
    private static Talent.Builder createBuilder(ResourceLocation location, boolean advancement) {
        Talent.Builder builder = Talent.builder();
        builder.icon(location.withPrefix("textures/talent/").withSuffix(".png"));
        builder.title("talent." + location.getNamespace() + "." + location.getPath().replace("/","."));
        if (advancement) builder.advancement(location.withPrefix("talents/"));
        return builder;
    }
}
