package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class    FTZSoundEvents {

    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(Registries.SOUND_EVENT, Fantazia.MODID);

    private static DeferredHolder<SoundEvent, SoundEvent> fixedRange(String path, float range) {
        return REGISTER.register(path, () -> SoundEvent.createFixedRangeEvent(Fantazia.location(path), range));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> variableRange(String path) {
        return REGISTER.register(path, () -> SoundEvent.createVariableRangeEvent(Fantazia.location(path)));
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_BEAT1 = fixedRange("ui.heartbeat1",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_BEAT2 = fixedRange("ui.heartbeat2",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> RINGING = fixedRange("ui.ringing",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> WHISPER = fixedRange("ui.whisper",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DENIED = fixedRange("ui.denied", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FURY_DISPEL = fixedRange("ui.fury_dispel",6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FURY_PROLONG = fixedRange("ui.fury_prolong",6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASHSTONE_ENTITY_READY = fixedRange("ui.dashstone_entity_ready", 4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> RUNE_CARVED = fixedRange("ui.rune_carved", 4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> AMPLIFICATION = fixedRange("ui.amplification", 4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> WISDOM_ABSORB = fixedRange("ui.wisdom_absorb", 4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> WISDOM_RELEASE = fixedRange("ui.wisdom_release", 4f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> DASH1_USE = fixedRange("dash.level1",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH2_USE = fixedRange("dash.level2",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH3_USE = fixedRange("dash.level3",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH1_RECHARGE = fixedRange("dash.level1_recharge",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH2_RECHARGE = fixedRange("dash.level2_recharge",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH3_RECHARGE = fixedRange("dash.level3_recharge",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DOUBLE_JUMP_USE = fixedRange("double_jump.use",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DOUBLE_JUMP_UNLOCK = fixedRange("double_jump.unlock",4f); // implemented
    
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_BLADE_BEGIN = fixedRange("fragile_blade.level0",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_BLADE_LOW = fixedRange("fragile_blade.level1",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_BLADE_MEDIUM = fixedRange("fragile_blade.level2",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_BLADE_HIGH = fixedRange("fragile_blade.level3",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_BLADE_MAXIMUM = fixedRange("fragile_blade.level4",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_BLADE_UNLEASH = fixedRange("fragile_blade.unleash",16f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> COMBAT_MELEE_BLOCK = fixedRange("combat.melee_block",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> COMBAT_ATTACK_STUNNED = fixedRange("combat.attack_stunned",8f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_BARRIER_DAMAGE = fixedRange("effect.barrier.hit",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_BARRIER_BREAK = fixedRange("effect.barrier.break",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_LAYERED_BARRIER_DAMAGE = fixedRange("effect.layered_barrier.hit", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_LAYERED_BARRIER_BREAK = fixedRange("effect.layered_barrier.break",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_HAEMORRHAGE_BLEEDING = fixedRange("effect.haemorrhage.bleeding",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_HAEMORRHAGE_APPLIED = fixedRange("effect.haemorrhage.applied",10f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_REFLECT = fixedRange("effect.reflect",20f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_DEFLECT = fixedRange("effect.deflect",20f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_DOOMED_APPLIED = fixedRange("mob_effect.doomed.applied",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_DOOMED_EXPIRED = fixedRange("mob_effect.doomed.expired",1f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> DAMNED_WRATH_TRIGGER = fixedRange("spell.damned_wrath.trigger",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DEVOUR_CAST = fixedRange("spell.devour.cast",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTANGLE_CAST = fixedRange("spell.entangle.cast",10f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REFLECT_TRIGGER = fixedRange("spell.reflect.trigger", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REWIND_CAST = fixedRange("spell.rewind.cast",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BOUNCE_CAST = variableRange("spell.bounce.cast"); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> VANISH_CAST = fixedRange("spell.vanish.cast", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> PUPPETEER_CAST = fixedRange("spell.puppeteer.cast", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ALL_IN_CAST = fixedRange("spell.all_in.cast", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ALL_IN_RECHARGE = fixedRange("spell.all_in.recharge", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> KNOCK_OUT_CAST = fixedRange("spell.knock_out.cast",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> KNOCK_OUT_IMPACT = fixedRange("spell.knock_out.impact",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> KNOCK_OUT_RECHARGE = fixedRange("spell.knock_out.recharge",4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REINFORCE_TRIGGER = fixedRange("spell.reinforce.trigger",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BOUNCE_RECHARGE = fixedRange("spell.bounce.recharge", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_STRIKE_TICK = fixedRange("spell.lightning_strike.tick", 4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_STRIKE_RECHARGE = fixedRange("spell.lightning_strike.recharge", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REWIND_RECHARGE = fixedRange("spell.rewind.recharge",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> WANDERERS_SPIRIT_MARK = fixedRange("spell.wanderers_spirit.mark",4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> WANDERERS_SPIRIT_CAST = fixedRange("spell.wanderers_spirit.cast",4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> SONIC_BOOM_CAST = fixedRange("spell.sonic_boom.cast",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> SONIC_BOOM_RECHARGE = fixedRange("spell.sonic_boom.recharge",4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> SONIC_BOOM_CLICKING = fixedRange("spell.sonic_boom.clicking",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> SUSTAIN_CAST = fixedRange("spell.sustain.cast", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> RING_OF_DOOM_CAST = fixedRange("spell.ring_of_doom.cast", 24f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_FALLEN_BREATH = fixedRange("entity.fallen_breath",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_EVADE = fixedRange("entity.evade",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_CRUSH_ON = variableRange("entity.crush_on");
    public static final DeferredHolder<SoundEvent, SoundEvent> DASHSTONE_APPEAR = fixedRange("entity.dashstone.appear",32f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASHSTONE_WIND = fixedRange("entity.dashstone.wind", 4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> PIMPILLO_FUSE_BURN = fixedRange("entity.pimpillo.fuse_burn", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> THROWN_PIN_BREAKING = fixedRange("entity.thrown_pin.break", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_FLY_BUZZING = fixedRange("entity.block_fly.buzzing", 6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_FLY_HURT = variableRange("entity.block_fly.hurt"); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_FLY_DEATH = variableRange("entity.block_fly.death"); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_FLY_PUNCH = variableRange("entity.block_fly.punch"); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> HATCHET_THROW = fixedRange("item.hatchet.throw",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> LEADERS_HORN_BLOW = fixedRange("item.leaders_horn.use",256f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ANCIENT_SPARK_COMBUST = fixedRange("item.ancient_spark.use",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> PIMPILLO_THROW = fixedRange("item.pimpillo.throw", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> THROWING_PIN_THROW = fixedRange("item.throwing_pin.throw", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_FLY_WINDUP = variableRange("item.block_fly.use"); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> ENGINEERING_TABLE_READ_PRINT = variableRange("block.engineering_table_read_print");

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
