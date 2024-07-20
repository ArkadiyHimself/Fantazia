package net.arkadiyhimself.fantazia.api;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.
            create(ForgeRegistries.SOUND_EVENTS, Fantazia.MODID);
    public static RegistryObject<SoundEvent> registerSoundEvent(String path, float range) {
        return SOUND_EVENTS.register(path, () -> SoundEvent.createFixedRangeEvent
                (Fantazia.res(path), range));
    }
    public static final RegistryObject<SoundEvent> HEART_BEAT; // implemented

    public static final RegistryObject<SoundEvent> DASH_DEFAULT; // implemented
    public static final RegistryObject<SoundEvent> DASH_SECOND; // implemented
    public static final RegistryObject<SoundEvent> DASH_FINAL; // implemented

    public static final RegistryObject<SoundEvent> DASH1_RECH; // implemented
    public static final RegistryObject<SoundEvent> DASH2_RECH; // implemented
    public static final RegistryObject<SoundEvent> DASH3_RECH; // implemented

    public static final RegistryObject<SoundEvent> FRAG_SWORD_BEGIN; // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_LOW; // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_MEDIUM; // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_HIGH; // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_MAXIMUM; // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_UNLEASH; // implemented

    public static final RegistryObject<SoundEvent> BLOCKED; // implemented

    public static final RegistryObject<SoundEvent> ATTACK_STUNNED; // implemented

    public static final RegistryObject<SoundEvent> BARRIER_HIT; // implemented
    public static final RegistryObject<SoundEvent> BARRIER_BREAK; // implemented

    public static final RegistryObject<SoundEvent> RINGING; // implemented

    public static final RegistryObject<SoundEvent> DEVOUR; // implemented
    public static final RegistryObject<SoundEvent> ENTANGLE; // implemented

    public static final RegistryObject<SoundEvent> DOOMED; // implemented
    public static final RegistryObject<SoundEvent> UNDOOMED; // implemented
    public static final RegistryObject<SoundEvent> FALLEN_BREATH; // implemented
    public static final RegistryObject<SoundEvent> WHISPER; // implemented

    public static final RegistryObject<SoundEvent> DENIED; // implemented

    public static final RegistryObject<SoundEvent> MYSTIC_MIRROR; // implemented
    public static final RegistryObject<SoundEvent> REFLECT; // implemented
    public static final RegistryObject<SoundEvent> DEFLECT; // implemented

    public static final RegistryObject<SoundEvent> HATCHET_THROW; // implemented
    public static final RegistryObject<SoundEvent> BLOODLOSS; // implemented
    public static final RegistryObject<SoundEvent> FLESH_RIPPING; // implemented
    public static final RegistryObject<SoundEvent> LEADERS_HORN; // implemented

    public static final RegistryObject<SoundEvent> FURY_DISPEL; // implemented
    public static final RegistryObject<SoundEvent> FURY_PROLONG; // implemented
    public static final RegistryObject<SoundEvent> BLOODLUST_AMULET; // implemented

    static {
        HEART_BEAT = registerSoundEvent("fury_heartbeat", 1f);

        DASH_DEFAULT = registerSoundEvent("dash.dash_default", 8f);
        DASH_SECOND = registerSoundEvent("dash.dash_second", 12f);
        DASH_FINAL = registerSoundEvent("dash.dash_final", 16f);

        DASH1_RECH = registerSoundEvent("dash.dash1_recharge", 1f);
        DASH2_RECH = registerSoundEvent("dash.dash2_recharge", 1f);
        DASH3_RECH = registerSoundEvent("dash.dash3_recharge", 1f);

        FRAG_SWORD_BEGIN = registerSoundEvent("fragile.sword.begin", 8f);
        FRAG_SWORD_LOW = registerSoundEvent("fragile.sword.low", 8f);
        FRAG_SWORD_MEDIUM = registerSoundEvent("fragile.sword.medium", 8f);
        FRAG_SWORD_HIGH = registerSoundEvent("fragile.sword.high", 8f);
        FRAG_SWORD_MAXIMUM = registerSoundEvent("fragile.sword.maximum", 12f);
        FRAG_SWORD_UNLEASH = registerSoundEvent("fragile.sword.unleashed", 16f);

        BLOCKED = registerSoundEvent("blocked", 12f);

        ATTACK_STUNNED = registerSoundEvent("attack_stunned", 8f);

        BARRIER_HIT = registerSoundEvent("barrier_hit", 8f);
        BARRIER_BREAK = registerSoundEvent("barrier_break", 8f);

        RINGING = registerSoundEvent("ringing", 1f);

        DEVOUR = registerSoundEvent("devour", 8f);
        ENTANGLE = registerSoundEvent("entangle", 10f);

        DOOMED = registerSoundEvent("doomed", 16f);
        UNDOOMED = registerSoundEvent("undoomed", 16f);
        FALLEN_BREATH = registerSoundEvent("fallen_breath", 12f);
        WHISPER = registerSoundEvent("whisper", 16f);

        DENIED = registerSoundEvent("denied", 16f);

        MYSTIC_MIRROR = registerSoundEvent("mystic_mirror", 12f);
        REFLECT = registerSoundEvent("reflect", 12f);
        DEFLECT = registerSoundEvent("deflect", 12f);

        HATCHET_THROW = registerSoundEvent("hatchet_throw", 12f);

        BLOODLOSS = registerSoundEvent("bloodloss", 8f);
        FLESH_RIPPING = registerSoundEvent("flesh_ripping", 10f);

        LEADERS_HORN = registerSoundEvent("leaders_horn", 256f);

        FURY_DISPEL = registerSoundEvent("fury_dispel", 6f);
        FURY_PROLONG = registerSoundEvent("fury_prolong", 6f);
        BLOODLUST_AMULET = registerSoundEvent("bloodlust_amulet", 6f);
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
