package net.arkadiyhimself.fantazia.data.talent;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class Talents {

    // abilities
    public static final ResourceLocation DOUBLE_JUMP = abilityAerial("double_jump"); // finished
    public static final ResourceLocation CAT_REFLEXES = abilityAerial("cat_reflexes"); // finished
    public static final ResourceLocation FINISHED_WINGS = abilityAerial("finished_wings"); // finished

    public static final ResourceLocation DASH1 = abilityDash(1); // finished
    public static final ResourceLocation DASH2 = abilityDash(2); // finished
    public static final ResourceLocation DASH3 = abilityDash(3); // finished

    public static final ResourceLocation RELENTLESS = abilityEuphoria("relentless"); // finished
    public static final ResourceLocation SAVAGE = abilityEuphoria("savage"); // finished

    public static final ResourceLocation MELEE_BLOCK = abilityMeleeBlock("melee_block"); // finished
    public static final ResourceLocation PARRY_HAEMORRHAGE = abilityMeleeBlock("parry_haemorrhage"); // finished
    public static final ResourceLocation PARRY_DISARM = abilityMeleeBlock("parry_disarm"); // finished

    public static final ResourceLocation WALL_CLIMBING = abilitySpiderPowers("wall_climbing"); // finished
    public static final ResourceLocation COBWEB_CLIMBING = abilitySpiderPowers("cobweb_climbing"); // finished
    public static final ResourceLocation POISON_ATTACK = abilitySpiderPowers("poison_attack"); // finished

    // spell casting
    public static final ResourceLocation MANA_RECYCLE1 = spellCastingManaRecycle(1); // finished
    public static final ResourceLocation MANA_RECYCLE2 = spellCastingManaRecycle(2); // finished
    public static final ResourceLocation MANA_RECYCLE3 = spellCastingManaRecycle(3); // finished

    public static final ResourceLocation PASSIVECASTER_SLOTS = spellCastingCurioSlotsUpgrade("passivecaster_slots"); // finished
    public static final ResourceLocation ACTIVECASTER_SLOTS = spellCastingCurioSlotsUpgrade("activecaster_slots"); // finished
    public static final ResourceLocation RUNE_SLOTS = spellCastingCurioSlotsUpgrade("rune_slots"); // finished

    public static final ResourceLocation AURA_RANGE_ADD = spellCastingMagicAssist("aura_range_add"); // finished
    public static final ResourceLocation CAST_RANGE_ADD = spellCastingMagicAssist("cast_range_add"); // finished
    public static final ResourceLocation SPELL_RECHARGE_REDUCE = spellCastingMagicAssist("spell_recharge_reduce"); // finished

    // stat modifiers
    public static final ResourceLocation NETHERITE_HELMET_REWARD = statModifiersNetheriteEquipmentReward("helmet"); // finished
    public static final ResourceLocation NETHERITE_CHESTPLATE_REWARD = statModifiersNetheriteEquipmentReward("chestplate"); // finished
    public static final ResourceLocation NETHERITE_LEGGINGS_REWARD = statModifiersNetheriteEquipmentReward("leggings"); // finished
    public static final ResourceLocation NETHERITE_BOOTS_REWARD = statModifiersNetheriteEquipmentReward("boots"); // finished
    public static final ResourceLocation NETHERITE_SWORD_REWARD = statModifiersNetheriteEquipmentReward("sword");
    public static final ResourceLocation NETHERITE_SHOVEL_REWARD = statModifiersNetheriteEquipmentReward("shovel");
    public static final ResourceLocation NETHERITE_PICKAXE_REWARD = statModifiersNetheriteEquipmentReward("pickaxe");
    public static final ResourceLocation NETHERITE_AXE_REWARD = statModifiersNetheriteEquipmentReward("axe");
    public static final ResourceLocation NETHERITE_HOE_REWARD = statModifiersNetheriteEquipmentReward("hoe");
    public static final ResourceLocation NETHERITE_HATCHET_REWARD = statModifiersNetheriteEquipmentReward("hatchet");

    // engineering
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE1 = engineeringToolCapacityUpgrade(1); // finished
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE2 = engineeringToolCapacityUpgrade(2); // finished
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE3 = engineeringToolCapacityUpgrade(3); // finished
    public static final ResourceLocation TOOL_CAPACITY_UPGRADE4 = engineeringToolCapacityUpgrade(4); // finished

    public static final ResourceLocation TOOL_DAMAGE_UPGRADE1 = engineeringToolDamageUpgrade(1); // finished
    public static final ResourceLocation TOOL_DAMAGE_UPGRADE2 = engineeringToolDamageUpgrade(2); // finished
    public static final ResourceLocation TOOL_DAMAGE_UPGRADE3 = engineeringToolDamageUpgrade(3); // finished
    public static final ResourceLocation TOOL_DAMAGE_UPGRADE4 = engineeringToolDamageUpgrade(4); // finished

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

    private static ResourceLocation statModifiersNetheriteEquipmentReward(String name) {
        return Fantazia.location("stat_modifiers/netherite_equipment_reward/" + name);
    }

    private static ResourceLocation engineeringToolCapacityUpgrade(int level) {
        return Fantazia.location("engineering/tool_capacity_upgrade/upgrade" + level);
    }

    private static ResourceLocation engineeringToolDamageUpgrade(int level) {
        return Fantazia.location("engineering/tool_damage_upgrade/upgrade" + level);
    }
}
