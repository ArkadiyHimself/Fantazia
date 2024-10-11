package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZSoundEvents {

    private FTZSoundEvents() {}

    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(Registries.SOUND_EVENT, Fantazia.MODID);

    private static DeferredHolder<SoundEvent, SoundEvent> fixedRange(String path, float range) {
        return REGISTER.register(path, () -> SoundEvent.createFixedRangeEvent(Fantazia.res(path), range));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> variableRange(String path) {
        return REGISTER.register(path, () -> SoundEvent.createVariableRangeEvent(Fantazia.res(path)));
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_BEAT1 = fixedRange("ui.heartbeat1",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_BEAT2 = fixedRange("ui.heartbeat2",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> RINGING = fixedRange("ui.ringing",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DOOMED = fixedRange("ui.doomed",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDOOMED = fixedRange("ui.undoomed",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> WHISPER = fixedRange("ui.whisper",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DENIED = fixedRange("ui.denied", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FURY_DISPEL = fixedRange("ui.fury_dispel",6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FURY_PROLONG = fixedRange("ui.fury_prolong",6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DAMNED_WRATH = fixedRange("ui.damned_wrath",6f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> DASH1 = fixedRange("dash.dash1",8f);// implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH2 = fixedRange("dash.dash2",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH3 = fixedRange("dash.dash3",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH1_RECHARGE = fixedRange("dash.dash1_recharge",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH2_RECHARGE = fixedRange("dash.dash2_recharge",1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH3_RECHARGE = fixedRange("dash.dash3_recharge",1f); // implemented
    
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_SWORD_BEGIN = fixedRange("fragile_sword.begin",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_SWORD_LOW = fixedRange("fragile_sword.low",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_SWORD_MEDIUM = fixedRange("fragile_sword.medium",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_SWORD_HIGH = fixedRange("fragile_sword.high",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_SWORD_MAXIMUM = fixedRange("fragile_sword.maximum",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAGILE_SWORD_UNLEASHED = fixedRange("fragile_sword.unleashed",16f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> COMBAT_MELEE_BLOCK = fixedRange("combat.melee_block",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> COMBAT_ATTACK_STUNNED = fixedRange("combat.attack_stunned",8f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_BARRIER_DAMAGE = fixedRange("effect.barrier.damage", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_BARRIER_BREAK = fixedRange("effect.barrier.break",16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_HAEMORRHAGE_BLOODLOSS = fixedRange("effect.haemorrhage.bloodloss",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_HAEMORRHAGE_FLESH_RIPPING = fixedRange("effect.haemorrhage.flesh_ripping",10f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_REFLECT = fixedRange("effect.reflect",20f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECT_DEFLECT = fixedRange("effect.deflect",20f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> DEVOUR_CAST = fixedRange("spell.devour.cast",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTANGLE_CAST = fixedRange("spell.entangle.cast",10f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REFLECT_CAST = fixedRange("spell.reflect.cast", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REWIND_CAST = fixedRange("spell.rewind.cast",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BOUNCE_CAST = variableRange("spell.bounce.cast"); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BOUNCE_RECHARGE = fixedRange("spell.bounce.recharge", 8f);
    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_STRIKE_TICK = fixedRange("spell.lightning_strike.tick", 4f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_STRIKE_RECHARGE = fixedRange("spell.lightning_strike.recharge", 8f); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_FALLEN_BREATH = fixedRange("entity.fallen_breath",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_EVADE = fixedRange("entity.evade",8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASHSTONE_WIND = variableRange("entity.dashstone.wind"); // implemented

    public static final DeferredHolder<SoundEvent, SoundEvent> HATCHET_THROW = fixedRange("item.hatchet.throw",12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> LEADERS_HORN = fixedRange("item.leaders_horn.sound",256f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ANCIENT_SPARK = fixedRange("item.ancient_spark.use",8f); // implemented

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
