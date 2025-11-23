package net.arkadiyhimself.fantazia.data.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public FantazicDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // fantazia
        tag(FTZDamageTypeTags.ELECTRIC).add(
                DamageTypes.LIGHTNING_BOLT,
                FTZDamageTypes.ELECTRIC
        );

        tag(FTZDamageTypeTags.ANCIENT_FLAME).add(
                FTZDamageTypes.ANCIENT_FLAME,
                FTZDamageTypes.ANCIENT_BURNING
        );

        tag(FTZDamageTypeTags.NO_HURT_SOUND).add(
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.OMINOUS_BELL
        );

        tag(FTZDamageTypeTags.NON_LETHAL).add(
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.NOT_SHAKING_SCREEN).add(
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.OMINOUS_BELL
        );

        tag(FTZDamageTypeTags.NOT_STOPPING_DASH).add(
                DamageTypes.CRAMMING,
                DamageTypes.DROWN,
                DamageTypes.STARVE,
                DamageTypes.GENERIC,
                DamageTypes.IN_WALL
        ).addTag(
                DamageTypeTags.NO_KNOCKBACK
        );

        tag(FTZDamageTypeTags.NOT_TURNING_RED).add(
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.PIERCES_BARRIER).add(
                DamageTypes.CRAMMING,
                DamageTypes.DROWN,
                DamageTypes.STARVE,
                DamageTypes.GENERIC,
                DamageTypes.GENERIC_KILL,
                DamageTypes.IN_WALL,
                FTZDamageTypes.SHOCKWAVE,
                FTZDamageTypes.BIFROST
        ).addTag(
                FTZDamageTypeTags.NON_LETHAL
        );

        tag(FTZDamageTypeTags.DEAFENING)
                .addTag(DamageTypeTags.IS_EXPLOSION);

        tag(DamageTypeTags.IS_EXPLOSION).add(
                FTZDamageTypes.PIMPILLO_EXPLOSION
        );

        tag(FTZDamageTypeTags.IGNORED_BY_RESTORE_SPELL).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.BIFROST
        );

        tag(FTZDamageTypeTags.IGNORED_BY_BIFROST).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.BLEEDING
        );

        tag(FTZDamageTypeTags.IGNORED_BY_HIDDEN_POTENTIAL).add(
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.MELEE_ATTACK).add(
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.PLAYER_ATTACK,
                FTZDamageTypes.BLOCK_FLY,
                FTZDamageTypes.PARRY
        );

        tag(FTZDamageTypeTags.BLOCKABLE).add(
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.PLAYER_ATTACK
        );

        tag(FTZDamageTypeTags.NEGATED_BY_SUSTAIN_SPELL).add(
                DamageTypes.WITHER
        );

        tag(FTZDamageTypeTags.SPAWNS_SCRAP_PARTICLES_ON_BLOCK_FLY)
                .addTag(FTZDamageTypeTags.MELEE_ATTACK)
                .addTag(DamageTypeTags.IS_PROJECTILE)
                .addTag(DamageTypeTags.IS_EXPLOSION);

        tag(FTZDamageTypeTags.MELEE_ATTACK_CAUSES_STUN).add(
                DamageTypes.MOB_ATTACK,
                DamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.PLAYER_ATTACK,
                FTZDamageTypes.BLOCK_FLY
        );

        tag(FTZDamageTypeTags.PARRY_ATTACK_CAUSES_STUN).add(
                FTZDamageTypes.PARRY
        );

        tag(FTZDamageTypeTags.REDUCED_BY_CAT_REFLEXES_TALENT)
                .addTag(DamageTypeTags.IS_FALL);

        tag(FTZDamageTypeTags.NEGATED_BY_THIRD_LEVEL_DASH_TALENT).add(
                DamageTypes.IN_WALL
        );

        tag(FTZDamageTypeTags.DOUBLE_DAMAGE_INCREASE_FOR_HIDDEN_POTENTIAL).add(
                FTZDamageTypes.PARRY
        );

        tag(FTZDamageTypeTags.CAN_BE_EVADED)
                .addTag(FTZDamageTypeTags.MELEE_ATTACK);

        tag(FTZDamageTypeTags.BLEEDING).add(
                FTZDamageTypes.BLEEDING
        );

        tag(FTZDamageTypeTags.IGNORED_BY_REINFORCE_SPELL).add(
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.IGNORED_BY_DAMNED_WRATH_SPELL).add(
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.IGNORED_BY_DIFFRACTION_AURA).add(
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.IGNORED_BY_TRANQUILIZE_AURA).add(
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.NO_COOLDOWN).add(
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.REMOVAL
        );

        tag(FTZDamageTypeTags.NO_DELAY_BEFORE_DECAYING_STUN_POINTS).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.BLEEDING
        );

        tag(FTZDamageTypeTags.NO_EUPHORIA_DECAY).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.BLEEDING
        );

        tag(FTZDamageTypeTags.FROM_RECHARGEABLE_TOOL).add(
                FTZDamageTypes.THROWN_PIN,
                FTZDamageTypes.PIMPILLO_EXPLOSION,
                FTZDamageTypes.BLOCK_FLY
        );

        // neo forge
        tag(Tags.DamageTypes.IS_MAGIC).add(
                FTZDamageTypes.BIFROST
        ).addTag(
                FTZDamageTypeTags.ANCIENT_FLAME
        );

        tag(Tags.DamageTypes.IS_ENVIRONMENT).addTag(
                FTZDamageTypeTags.ANCIENT_FLAME
        );

        tag(Tags.DamageTypes.NO_FLINCH).addTag(
                FTZDamageTypeTags.NOT_SHAKING_SCREEN
        );

        tag(Tags.DamageTypes.IS_PHYSICAL).add(
                FTZDamageTypes.PARRY,
                FTZDamageTypes.BLOCK_FLY,
                FTZDamageTypes.THROWN_PIN,
                FTZDamageTypes.HATCHET
        );

        // minecraft
        tag(DamageTypeTags.BYPASSES_ARMOR).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.ANCIENT_FLAME,
                FTZDamageTypes.ANCIENT_BURNING,
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.FROZEN,
                FTZDamageTypes.PARRY,
                FTZDamageTypes.ELECTRIC,
                FTZDamageTypes.BIFROST,
                FTZDamageTypes.OMINOUS_BELL
        );

        tag(DamageTypeTags.BYPASSES_COOLDOWN).add(
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.OMINOUS_BELL,
                FTZDamageTypes.BIFROST
        );

        tag(DamageTypeTags.BYPASSES_EFFECTS).add(
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.OMINOUS_BELL,
                FTZDamageTypes.ANCIENT_FLAME,
                FTZDamageTypes.ANCIENT_BURNING,
                FTZDamageTypes.BIFROST
        );

        tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.OMINOUS_BELL,
                FTZDamageTypes.BIFROST
        );

        tag(DamageTypeTags.NO_IMPACT).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.ANCIENT_FLAME,
                FTZDamageTypes.ANCIENT_BURNING,
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.FROZEN,
                FTZDamageTypes.ELECTRIC,
                FTZDamageTypes.BIFROST,
                FTZDamageTypes.OMINOUS_BELL
        );

        tag(DamageTypeTags.NO_KNOCKBACK).add(
                FTZDamageTypes.REMOVAL,
                FTZDamageTypes.ANCIENT_FLAME,
                FTZDamageTypes.ANCIENT_BURNING,
                FTZDamageTypes.BLEEDING,
                FTZDamageTypes.FROZEN,
                FTZDamageTypes.ELECTRIC,
                FTZDamageTypes.BIFROST,
                FTZDamageTypes.OMINOUS_BELL,
                FTZDamageTypes.PIMPILLO_EXPLOSION,
                FTZDamageTypes.THROWN_PIN
        );

        tag(DamageTypeTags.IS_PROJECTILE).add(
                FTZDamageTypes.HATCHET,
                FTZDamageTypes.SIMPLE_CHASING_PROJECTILE,
                FTZDamageTypes.THROWN_PIN
        );

        tag(DamageTypeTags.IS_FREEZING).add(
                FTZDamageTypes.FROZEN
        );

        tag(DamageTypeTags.PANIC_CAUSES).add(
                FTZDamageTypes.PIMPILLO_EXPLOSION
        );
    }
}
