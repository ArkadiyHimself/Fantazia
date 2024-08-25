package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FTZSoundEvents {
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Fantazia.MODID);
    private static RegistryObject<SoundEvent> soundEvent(String path, float range) {
        return REGISTER.register(path, () -> SoundEvent.createFixedRangeEvent(Fantazia.res(path), range));
    }
    public static RegistryObject<SoundEvent> HEART_BEAT1 = soundEvent("fury_heartbeat1", 1f); // implemented
    public static RegistryObject<SoundEvent> HEART_BEAT2 = soundEvent("fury_heartbeat2", 1f); // implemented
    public static RegistryObject<SoundEvent> DASH_DEFAULT = soundEvent("dash.dash_default", 8f);// implemented
    public static RegistryObject<SoundEvent> DASH_SECOND = soundEvent("dash.dash_second", 12f); // implemented
    public static RegistryObject<SoundEvent> DASH_FINAL = soundEvent("dash.dash_final", 16f); // implemented
    public static RegistryObject<SoundEvent> DASH1_RECHARGE = soundEvent("dash.dash1_recharge", 1f); // implemented
    public static RegistryObject<SoundEvent> DASH2_RECHARGE = soundEvent("dash.dash2_recharge", 1f); // implemented
    public static RegistryObject<SoundEvent> DASH3_RECHARGE = soundEvent("dash.dash3_recharge", 1f); // implemented
    public static RegistryObject<SoundEvent> FRAG_SWORD_BEGIN = soundEvent("fragile.sword.begin", 8f); // implemented
    public static RegistryObject<SoundEvent> FRAG_SWORD_LOW = soundEvent("fragile.sword.low", 8f); // implemented
    public static RegistryObject<SoundEvent> FRAG_SWORD_MEDIUM = soundEvent("fragile.sword.medium", 8f); // implemented
    public static RegistryObject<SoundEvent> FRAG_SWORD_HIGH = soundEvent("fragile.sword.high", 8f); // implemented
    public static RegistryObject<SoundEvent> FRAG_SWORD_MAXIMUM = soundEvent("fragile.sword.maximum", 12f); // implemented
    public static RegistryObject<SoundEvent> FRAG_SWORD_UNLEASHED = soundEvent("fragile.sword.unleashed", 16f); // implemented
    public static RegistryObject<SoundEvent> BLOCKED = soundEvent("blocked", 12f); // implemented
    public static RegistryObject<SoundEvent> ATTACK_STUNNED = soundEvent("attack_stunned", 8f); // implemented
    public static RegistryObject<SoundEvent> BARRIER_HIT = soundEvent("barrier_hit", 12f); // implemented
    public static RegistryObject<SoundEvent> BARRIER_BREAK = soundEvent("barrier_break", 16f);; // implemented
    public static RegistryObject<SoundEvent> RINGING = soundEvent("ringing", 1f); // implemented
    public static RegistryObject<SoundEvent> DEVOUR = soundEvent("devour", 12f); // implemented
    public static RegistryObject<SoundEvent> ENTANGLE = soundEvent("entangle", 10f); // implemented
    public static RegistryObject<SoundEvent> DOOMED = soundEvent("doomed", 1f); // implemented
    public static RegistryObject<SoundEvent> UNDOOMED = soundEvent("undoomed", 1f); // implemented
    public static RegistryObject<SoundEvent> FALLEN_BREATH = soundEvent("fallen_breath", 12f); // implemented
    public static RegistryObject<SoundEvent> WHISPER = soundEvent("whisper", 16f); // implemented
    public static RegistryObject<SoundEvent> DENIED = soundEvent("denied", 16f); // implemented
    public static RegistryObject<SoundEvent> MYSTIC_MIRROR = soundEvent("mystic_mirror", 12f); // implemented
    public static RegistryObject<SoundEvent> REFLECT = soundEvent("reflect", 12f); // implemented
    public static RegistryObject<SoundEvent> DEFLECT = soundEvent("deflect", 12f); // implemented
    public static RegistryObject<SoundEvent> HATCHET_THROW = soundEvent("hatchet_throw", 12f); // implemented
    public static RegistryObject<SoundEvent> BLOODLOSS = soundEvent("bloodloss", 8f); // implemented
    public static RegistryObject<SoundEvent> FLESH_RIPPING = soundEvent("flesh_ripping", 10f); // implemented
    public static RegistryObject<SoundEvent> LEADERS_HORN = soundEvent("leaders_horn", 256f); // implemented
    public static RegistryObject<SoundEvent> FURY_DISPEL = soundEvent("fury_dispel", 6f); // implemented
    public static RegistryObject<SoundEvent> FURY_PROLONG = soundEvent("fury_prolong", 6f); // implemented
    public static RegistryObject<SoundEvent> BLOODLUST_AMULET = soundEvent("bloodlust_amulet", 6f);; // implemented
    public static RegistryObject<SoundEvent> EVASION = soundEvent("evasion", 8f); // implemented
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
