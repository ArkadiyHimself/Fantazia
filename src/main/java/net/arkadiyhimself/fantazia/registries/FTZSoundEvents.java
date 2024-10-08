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

    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_BEAT1 = fixedRange("ui.fury_heartbeat1", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_BEAT2 = fixedRange("ui.fury_heartbeat2", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH_DEFAULT = fixedRange("dash.dash_default", 8f);// implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH_SECOND = fixedRange("dash.dash_second", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH_FINAL = fixedRange("dash.dash_final", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH1_RECHARGE = fixedRange("dash.dash1_recharge", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH2_RECHARGE = fixedRange("dash.dash2_recharge", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH3_RECHARGE = fixedRange("dash.dash3_recharge", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAG_SWORD_BEGIN = fixedRange("fragile_sword.begin", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAG_SWORD_LOW = fixedRange("fragile_sword.low", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAG_SWORD_MEDIUM = fixedRange("fragile_sword.medium", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAG_SWORD_HIGH = fixedRange("fragile_sword.high", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAG_SWORD_MAXIMUM = fixedRange("fragile_sword.maximum", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FRAG_SWORD_UNLEASHED = fixedRange("fragile_sword.unleashed", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCKED = fixedRange("combat.melee_block", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ATTACK_STUNNED = fixedRange("combat.attack_stunned", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BARRIER_HIT = fixedRange("barrier.hit", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BARRIER_BREAK = fixedRange("barrier.break", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> RINGING = fixedRange("ui.ringing", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DEVOUR = fixedRange("spell.devour", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ENTANGLE = fixedRange("spell.entangle", 10f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DOOMED = fixedRange("ui.doomed", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDOOMED = fixedRange("ui.undoomed", 1f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FALLEN_BREATH = fixedRange("entity.fallen_breath", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> WHISPER = fixedRange("ui.whisper", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DENIED = fixedRange("ui.denied", 16f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> MYSTIC_MIRROR = fixedRange("spell.mystic_mirror", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REFLECT = fixedRange("targeted.reflect", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECT = fixedRange("targeted.deflect", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> HATCHET_THROW = fixedRange("item.hatchet.throw", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOODLOSS = fixedRange("entity.bloodloss", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FLESH_RIPPING = fixedRange("entity.flesh_ripping", 10f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> LEADERS_HORN = fixedRange("item.leaders_horn.sound", 256f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FURY_DISPEL = fixedRange("ui.fury_dispel", 6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> FURY_PROLONG = fixedRange("ui.fury_prolong", 6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOODLUST_AMULET = fixedRange("ui.bloodlust_amulet", 6f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> EVASION = fixedRange("entity.evasion", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> REWIND = fixedRange("spell.rewind", 12f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> ANCIENT_SPARK = fixedRange("item.ancient_spark.use", 8f); // implemented
    public static final DeferredHolder<SoundEvent, SoundEvent> BOUNCE = variableRange("spell.bounce");
    public static final DeferredHolder<SoundEvent, SoundEvent> WIND = variableRange("ambient.wind");

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
