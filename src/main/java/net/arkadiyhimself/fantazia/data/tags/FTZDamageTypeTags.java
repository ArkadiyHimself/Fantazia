package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface FTZDamageTypeTags {

    TagKey<DamageType> NO_HURT_SOUND = create("no_hurt_sound");
    TagKey<DamageType> NOT_SHAKING_SCREEN = create("not_shaking_screen");
    TagKey<DamageType> NOT_TURNING_RED = create("not_turning_red");
    TagKey<DamageType> NON_LETHAL = create("non_lethal");
    TagKey<DamageType> PIERCES_BARRIER = create("pierces_barrier");
    TagKey<DamageType> NOT_STOPPING_DASH = create("not_stopping_dash");
    TagKey<DamageType> ELECTRIC = create("electric");
    TagKey<DamageType> ANCIENT_FLAME = create("ancient_flame");
    TagKey<DamageType> DEAFENING = create("deafening");
    TagKey<DamageType> IGNORED_BY_RESTORE_SPELL = create("ignored_by_restore_spell");
    TagKey<DamageType> IGNORED_BY_BIFROST = create("ignored_by_bifrost");
    TagKey<DamageType> IGNORED_BY_HIDDEN_POTENTIAL = create("ignored_by_hidden_potential");
    TagKey<DamageType> MELEE_ATTACK = create("melee_attack");
    TagKey<DamageType> BLOCKABLE = create("blockable");
    TagKey<DamageType> NEGATED_BY_SUSTAIN_SPELL = create("negated_by_sustain_spell");
    TagKey<DamageType> SPAWNS_SCRAP_PARTICLES_ON_BLOCK_FLY = create("spawns_scrap_particles_on_block_fly");
    TagKey<DamageType> MELEE_ATTACK_CAUSES_STUN = create("melee_attack_causes_stun");
    TagKey<DamageType> PARRY_ATTACK_CAUSES_STUN = create("parry_attack_causes_stun");
    TagKey<DamageType> REDUCED_BY_CAT_REFLEXES_TALENT = create("reduced_by_cat_reflexes_talent");
    TagKey<DamageType> NEGATED_BY_THIRD_LEVEL_DASH_TALENT = create("negated_by_third_level_dash_talent");
    TagKey<DamageType> DOUBLE_DAMAGE_INCREASE_FOR_HIDDEN_POTENTIAL = create("double_damage_increase_for_hidden_potential");
    TagKey<DamageType> CAN_BE_EVADED = create("can_be_evaded");
    TagKey<DamageType> BLEEDING = create("bleeding");
    TagKey<DamageType> IGNORED_BY_REINFORCE_SPELL = create("ignored_by_reinforce_spell");
    TagKey<DamageType> IGNORED_BY_DAMNED_WRATH_SPELL = create("ignored_by_damned_wrath_spell");
    TagKey<DamageType> IGNORED_BY_DIFFRACTION_AURA = create("ignored_by_diffraction_aura");
    TagKey<DamageType> IGNORED_BY_TRANQUILIZE_AURA = create("ignored_by_tranquilize_aura");
    TagKey<DamageType> NO_COOLDOWN = create("no_cooldown");
    TagKey<DamageType> NO_DELAY_BEFORE_DECAYING_STUN_POINTS = create("no_delay_before_decaying_stun_points");
    TagKey<DamageType> NO_EUPHORIA_DECAY = create("no_euphoria_decay");
    TagKey<DamageType> FROM_RECHARGEABLE_TOOL = create("from_rechargeable_tool");

    private static TagKey<DamageType> create(String pName) {
        return TagKey.create(Registries.DAMAGE_TYPE, Fantazia.location(pName));
    }
}
