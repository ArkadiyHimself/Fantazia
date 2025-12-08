package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FantazicSoundDefinitionsProvider extends SoundDefinitionsProvider {

    public FantazicSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, Fantazia.MODID, helper);
    }

    @Override
    public void registerSounds() {
        // block
        defaultSubtitle(FTZSoundEvents.ENGINEERING_TABLE_READ_PRINT, "block/engineering_table_read_print");

        // combat
        defaultSubtitle(FTZSoundEvents.COMBAT_ATTACK_STUNNED,"combat/attack_stunned");
        defaultSubtitle(FTZSoundEvents.COMBAT_MELEE_BLOCK,"combat/melee_block");

        // dash
        defaultSubtitle(FTZSoundEvents.DASH1_USE,"dash/level1_use");
        specifySubtitle(FTZSoundEvents.DASH1_RECHARGE,"dash/level1_recharge","fantazia.subtitles.dash.dash_recharge");
        defaultSubtitle(FTZSoundEvents.DASH2_USE,"dash/level2_use");
        specifySubtitle(FTZSoundEvents.DASH2_RECHARGE,"dash/level2_recharge","fantazia.subtitles.dash.dash_recharge");
        defaultSubtitle(FTZSoundEvents.DASH3_USE,"dash/level3_use");
        specifySubtitle(FTZSoundEvents.DASH3_RECHARGE,"dash/level3_recharge","fantazia.subtitles.dash.dash_recharge");

        // double jump
        defaultSubtitle(FTZSoundEvents.DOUBLE_JUMP_USE,"double_jump/use");
        defaultSubtitle(FTZSoundEvents.DOUBLE_JUMP_UNLOCK,"double_jump/unlock");

        // entity
        defaultSubtitle(FTZSoundEvents.BLOCK_FLY_BUZZING,"entity/block_fly/buzzing");
        defaultSubtitle(FTZSoundEvents.BLOCK_FLY_DEATH,"entity/block_fly/death");
        defaultSubtitleMultipleVariations(FTZSoundEvents.BLOCK_FLY_HURT,"entity/block_fly/hurt",2);
        defaultSubtitleMultipleVariations(FTZSoundEvents.BLOCK_FLY_PUNCH,"entity/block_fly/punch",3);
        defaultSubtitle(FTZSoundEvents.DASHSTONE_APPEAR,"entity/dashstone/appear");
        defaultSubtitleMultipleVariations(FTZSoundEvents.DASHSTONE_WIND, "entity/dashstone/wind",5);
        defaultSubtitle(FTZSoundEvents.PIMPILLO_FUSE_BURN,"entity/pimpillo/fuse_burn");
        defaultSubtitleMultipleVariations(FTZSoundEvents.THROWN_PIN_BREAKING,"entity/thrown_pin/break",3);
        defaultSubtitle(FTZSoundEvents.ENTITY_EVADE,"entity/evade");
        defaultSubtitle(FTZSoundEvents.ENTITY_FALLEN_BREATH,"entity/fallen_breath");
        defaultSubtitleMultipleVariations(FTZSoundEvents.ENTITY_CRUSH_ON, "entity/crush_on/variant", 4);

        // fragile blade
        defaultSubtitle(FTZSoundEvents.FRAGILE_BLADE_BEGIN,"fragile_blade/level0");
        defaultSubtitle(FTZSoundEvents.FRAGILE_BLADE_LOW,"fragile_blade/level1");
        defaultSubtitle(FTZSoundEvents.FRAGILE_BLADE_MEDIUM,"fragile_blade/level2");
        defaultSubtitle(FTZSoundEvents.FRAGILE_BLADE_HIGH,"fragile_blade/level3");
        defaultSubtitle(FTZSoundEvents.FRAGILE_BLADE_MAXIMUM,"fragile_blade/level4");
        defaultSubtitle(FTZSoundEvents.FRAGILE_BLADE_UNLEASH,"fragile_blade/unleash");

        // item
        defaultSubtitleMultipleVariations(FTZSoundEvents.ANCIENT_SPARK_COMBUST,"item/ancient_spark/use",2);
        defaultSubtitleMultipleVariations(FTZSoundEvents.PIMPILLO_THROW,"item/pimpillo/throw",3);
        defaultSubtitle(FTZSoundEvents.BLOCK_FLY_WINDUP,"item/block_fly_use");
        defaultSubtitle(FTZSoundEvents.HATCHET_THROW,"item/hatchet_throw");
        defaultSubtitle(FTZSoundEvents.LEADERS_HORN_BLOW,"item/leaders_horn_use");
        defaultSubtitle(FTZSoundEvents.THROWING_PIN_THROW,"item/throwing_pin_throw");

        // mob effect
        defaultSubtitle(FTZSoundEvents.EFFECT_BARRIER_BREAK,"mob_effect/barrier/break");
        defaultSubtitle(FTZSoundEvents.EFFECT_DOOMED_APPLIED,"mob_effect/doomed/applied");
        defaultSubtitle(FTZSoundEvents.EFFECT_DOOMED_EXPIRED,"mob_effect/doomed/expired");
        defaultSubtitleMultipleVariations(FTZSoundEvents.EFFECT_BARRIER_DAMAGE,"mob_effect/barrier/hit",3);
        defaultSubtitle(FTZSoundEvents.EFFECT_HAEMORRHAGE_APPLIED,"mob_effect/haemorrhage/applied");
        defaultSubtitleMultipleVariations(FTZSoundEvents.EFFECT_HAEMORRHAGE_BLEEDING,"mob_effect/haemorrhage/bleeding",5);
        defaultSubtitle(FTZSoundEvents.EFFECT_LAYERED_BARRIER_BREAK,"mob_effect/layered_barrier/break");
        defaultSubtitleMultipleVariations(FTZSoundEvents.EFFECT_LAYERED_BARRIER_DAMAGE,"mob_effect/layered_barrier/hit",2);
        defaultSubtitle(FTZSoundEvents.EFFECT_DEFLECT,"mob_effect/deflect");
        defaultSubtitle(FTZSoundEvents.EFFECT_REFLECT,"mob_effect/reflect");

        // spell
        defaultSubtitle(FTZSoundEvents.ALL_IN_CAST,"spell/all_in/cast");
        defaultSubtitle(FTZSoundEvents.ALL_IN_RECHARGE,"spell/all_in/recharge");
        defaultSubtitle(FTZSoundEvents.BOUNCE_CAST,"spell/bounce/cast");
        defaultSubtitle(FTZSoundEvents.BOUNCE_RECHARGE,"spell/bounce/recharge");
        defaultSubtitle(FTZSoundEvents.KNOCK_OUT_CAST,"spell/knock_out/cast");
        defaultSubtitle(FTZSoundEvents.KNOCK_OUT_IMPACT,"spell/knock_out/impact");
        defaultSubtitle(FTZSoundEvents.KNOCK_OUT_RECHARGE,"spell/knock_out/recharge");
        defaultSubtitleMultipleVariations(FTZSoundEvents.LIGHTNING_STRIKE_TICK,"spell/lightning_strike/electro",6);
        defaultSubtitle(FTZSoundEvents.LIGHTNING_STRIKE_RECHARGE,"spell/lightning_strike/recharge");
        defaultSubtitle(FTZSoundEvents.REWIND_CAST,"spell/rewind/cast");
        defaultSubtitle(FTZSoundEvents.REWIND_RECHARGE,"spell/rewind/recharge");
        defaultSubtitle(FTZSoundEvents.WANDERERS_SPIRIT_CAST,"spell/wanderers_spirit/cast");
        defaultSubtitle(FTZSoundEvents.WANDERERS_SPIRIT_MARK,"spell/wanderers_spirit/mark");
        defaultSubtitle(FTZSoundEvents.DAMNED_WRATH_TRIGGER,"spell/damned_wrath_trigger");
        defaultSubtitle(FTZSoundEvents.DEVOUR_CAST,"spell/devour_cast");
        defaultSubtitle(FTZSoundEvents.ENTANGLE_CAST,"spell/entangle_cast");
        defaultSubtitle(FTZSoundEvents.PUPPETEER_CAST,"spell/puppeteer_cast");
        defaultSubtitle(FTZSoundEvents.REFLECT_TRIGGER,"spell/reflect_trigger");
        defaultSubtitle(FTZSoundEvents.REINFORCE_TRIGGER,"spell/reinforce_trigger");
        defaultSubtitle(FTZSoundEvents.RING_OF_DOOM_CAST,"spell/ring_of_doom_cast");
        defaultSubtitle(FTZSoundEvents.SONIC_BOOM_RECHARGE,"spell/sonic_boom_recharge");
        defaultSubtitle(FTZSoundEvents.VANISH_CAST,"spell/vanish_cast");
        vanillaSoundWithVariations(FTZSoundEvents.SONIC_BOOM_CLICKING,"block/sculk_sensor/sculk_clicking",6);
        vanillaSoundWithVariations(FTZSoundEvents.SONIC_BOOM_CAST,"mob/warden/sonic_boom",4);

        // ui
        defaultSubtitleMultipleVariations(FTZSoundEvents.WHISPER,"ui/whisper/variant",5);
        defaultSubtitle(FTZSoundEvents.WISDOM_ABSORB,"ui/wisdom_transform/absorb");
        defaultSubtitle(FTZSoundEvents.WISDOM_RELEASE,"ui/wisdom_transform/release");
        defaultSubtitle(FTZSoundEvents.AMPLIFICATION,"ui/amplification");
        defaultSubtitle(FTZSoundEvents.DASHSTONE_ENTITY_READY,"ui/dashstone_entity_ready");
        defaultSubtitle(FTZSoundEvents.DENIED,"ui/denied");
        defaultSubtitle(FTZSoundEvents.FURY_DISPEL,"ui/fury_dispel");
        defaultSubtitle(FTZSoundEvents.FURY_PROLONG,"ui/fury_prolong");
        specifySubtitle(FTZSoundEvents.HEART_BEAT1,"ui/heart_beat1","fantazia.subtitles.ui.heartbeat");
        specifySubtitle(FTZSoundEvents.HEART_BEAT2,"ui/heart_beat2","fantazia.subtitles.ui.heartbeat");
        defaultSubtitle(FTZSoundEvents.RINGING,"ui/ringing");
        defaultSubtitle(FTZSoundEvents.RUNE_CARVED,"ui/rune_carved");
    }

    private void specifySubtitle(DeferredHolder<SoundEvent, SoundEvent> holder, String soundFile, String subtitle) {
        SoundDefinition definition = SoundDefinition.definition()
                .with(sound(Fantazia.location(soundFile)))
                .subtitle(subtitle);
        this.add(holder, definition);
    }

    private void vanillaSoundWithVariations(DeferredHolder<SoundEvent, SoundEvent> holder, String basicPath, int amount) {
        SoundDefinition definition = SoundDefinition.definition().subtitle(subtitleLanguageKey(holder));
        for (int i = 1; i <= amount; i++)
            definition.with(sound(ResourceLocation.withDefaultNamespace(basicPath + i)));
        this.add(holder, definition);
    }

    private void defaultSubtitleMultipleVariations(DeferredHolder<SoundEvent, SoundEvent> holder, String basicPath, int amount) {
        SoundDefinition definition = SoundDefinition.definition().subtitle(subtitleLanguageKey(holder));
        for (int i = 1; i <= amount; i++)
            definition.with(sound(Fantazia.location(basicPath + i)));
        this.add(holder, definition);
    }

    private void defaultSubtitle(DeferredHolder<SoundEvent, SoundEvent> holder, String soundFile) {
        SoundDefinition definition = SoundDefinition.definition()
                .with(sound(Fantazia.location(soundFile)))
                .subtitle(subtitleLanguageKey(holder));
        this.add(holder, definition);
    }

    public static String subtitleLanguageKey(DeferredHolder<SoundEvent, SoundEvent> holder) {
        return holder.getId().withPrefix("subtitles.").toLanguageKey();
    }
}
