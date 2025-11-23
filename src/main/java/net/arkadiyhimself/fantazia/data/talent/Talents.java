package net.arkadiyhimself.fantazia.data.talent;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;

public class Talents {

    // abilities
    public static final ResourceLocation DOUBLE_JUMP = abilityAerial("double_jump");
    public static final ResourceLocation CAT_REFLEXES = abilityAerial("cat_reflexes");
    public static final ResourceLocation FINISHED_WINGS = abilityAerial("finished_wings");

    public static final ResourceLocation DASH1 = abilityDash(1);
    public static final ResourceLocation DASH2 = abilityDash(2);
    public static final ResourceLocation DASH3 = abilityDash(3);

    public static final ResourceLocation RELENTLESS = abilityEuphoria("relentless");
    public static final ResourceLocation SAVAGE = abilityEuphoria("savage");

    public static final ResourceLocation MELEE_BLOCK = abilityMeleeBlock("melee_block");
    public static final ResourceLocation PARRY_HAEMORRHAGE = abilityMeleeBlock("parry_haemorrhage");
    public static final ResourceLocation PARRY_DISARM = abilityMeleeBlock("parry_disarm");

    public static final ResourceLocation WALL_CLIMBING = abilitySpiderPowers("wall_climbing");
    public static final ResourceLocation COBWEB_CLIMBING = abilitySpiderPowers("cobweb_climbing");
    public static final ResourceLocation POISON_ATTACK = abilitySpiderPowers("poison_attack");

    // spell casting
    public static final ResourceLocation MANA_RECYCLE1 = spellCastingManaRecycle(1);
    public static final ResourceLocation MANA_RECYCLE2 = spellCastingManaRecycle(2);
    public static final ResourceLocation MANA_RECYCLE3 = spellCastingManaRecycle(3);

    public static final ResourceLocation PASSIVECASTER_SLOTS = spellCastingCurioSlotsUpgrade("passivecaster_slots");
    public static final ResourceLocation ACTIVECASTER_SLOTS = spellCastingCurioSlotsUpgrade("activecaster_slots");
    public static final ResourceLocation RUNE_SLOTS = spellCastingCurioSlotsUpgrade("rune_slots");

    public static final ResourceLocation AURA_RANGE_ADD = spellCastingMagicAssist("aura_range_add");
    public static final ResourceLocation CAST_RANGE_ADD = spellCastingMagicAssist("cast_range_add");
    public static final ResourceLocation SPELL_RECHARGE_REDUCE = spellCastingMagicAssist("spell_recharge_reduce");

    // stat modifiers
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE1 = statModifiersToolCapacityUpgrade(1);
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE2 = statModifiersToolCapacityUpgrade(2);
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE3 = statModifiersToolCapacityUpgrade(3);
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE4 = statModifiersToolCapacityUpgrade(4);

    private static ResourceLocation abilityAerial(String name) {
        return Fantazia.location("abilities/aerial/" + name);
    }

    private static ResourceLocation abilityDash(int level) {
        return Fantazia.location("abilities/dash/dash" + level);
    }

    private static ResourceLocation abilityEuphoria(String name) {
        return Fantazia.location("abilities/euphoria/" + name);
    }

    private static ResourceLocation abilityMeleeBlock(String name) {
        return Fantazia.location("abilities/melee_block/" + name);
    }

    private static ResourceLocation abilitySpiderPowers(String name) {
        return Fantazia.location("abilities/spider_powers/" + name);
    }

    private static ResourceLocation spellCastingManaRecycle(int level) {
        return Fantazia.location("spellcasting/mana_recycle/mana_recycle" + level);
    }

    private static ResourceLocation spellCastingCurioSlotsUpgrade(String name) {
        return Fantazia.location("spellcasting/spellcasting1/" + name);
    }

    private static ResourceLocation spellCastingMagicAssist(String name) {
        return Fantazia.location("spellcasting/spellcasting2/" + name);
    }

    private static ResourceLocation statModifiersToolCapacityUpgrade(int level) {
        return Fantazia.location("stat_modifiers/capacity_upgrade/upgrade" + level);
    }
}
