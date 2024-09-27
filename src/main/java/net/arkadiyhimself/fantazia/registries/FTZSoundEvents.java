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
    public static final RegistryObject<SoundEvent> HEART_BEAT1 = soundEvent("ui.fury_heartbeat1", 1f); // implemented
    public static final RegistryObject<SoundEvent> HEART_BEAT2 = soundEvent("ui.fury_heartbeat2", 1f); // implemented
    public static final RegistryObject<SoundEvent> DASH_DEFAULT = soundEvent("dash.dash_default", 8f);// implemented
    public static final RegistryObject<SoundEvent> DASH_SECOND = soundEvent("dash.dash_second", 12f); // implemented
    public static final RegistryObject<SoundEvent> DASH_FINAL = soundEvent("dash.dash_final", 16f); // implemented
    public static final RegistryObject<SoundEvent> DASH1_RECHARGE = soundEvent("dash.dash1_recharge", 1f); // implemented
    public static final RegistryObject<SoundEvent> DASH2_RECHARGE = soundEvent("dash.dash2_recharge", 1f); // implemented
    public static final RegistryObject<SoundEvent> DASH3_RECHARGE = soundEvent("dash.dash3_recharge", 1f); // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_BEGIN = soundEvent("fragile_sword.begin", 8f); // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_LOW = soundEvent("fragile_sword.low", 8f); // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_MEDIUM = soundEvent("fragile_sword.medium", 8f); // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_HIGH = soundEvent("fragile_sword.high", 8f); // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_MAXIMUM = soundEvent("fragile_sword.maximum", 12f); // implemented
    public static final RegistryObject<SoundEvent> FRAG_SWORD_UNLEASHED = soundEvent("fragile_sword.unleashed", 16f); // implemented
    public static final RegistryObject<SoundEvent> BLOCKED = soundEvent("combat.melee_block", 12f); // implemented
    public static final RegistryObject<SoundEvent> ATTACK_STUNNED = soundEvent("combat.attack_stunned", 8f); // implemented
    public static final RegistryObject<SoundEvent> BARRIER_HIT = soundEvent("barrier.hit", 12f); // implemented
    public static final RegistryObject<SoundEvent> BARRIER_BREAK = soundEvent("barrier.break", 16f);; // implemented
    public static final RegistryObject<SoundEvent> RINGING = soundEvent("ui.ringing", 1f); // implemented
    public static final RegistryObject<SoundEvent> DEVOUR = soundEvent("spell.devour", 12f); // implemented
    public static final RegistryObject<SoundEvent> ENTANGLE = soundEvent("spell.entangle", 10f); // implemented
    public static final RegistryObject<SoundEvent> DOOMED = soundEvent("ui.doomed", 1f); // implemented
    public static final RegistryObject<SoundEvent> UNDOOMED = soundEvent("ui.undoomed", 1f); // implemented
    public static final RegistryObject<SoundEvent> FALLEN_BREATH = soundEvent("entity.fallen_breath", 12f); // implemented
    public static final RegistryObject<SoundEvent> WHISPER = soundEvent("ui.whisper", 16f); // implemented
    public static final RegistryObject<SoundEvent> DENIED = soundEvent("ui.denied", 16f); // implemented
    public static final RegistryObject<SoundEvent> MYSTIC_MIRROR = soundEvent("spell.mystic_mirror", 12f); // implemented
    public static final RegistryObject<SoundEvent> REFLECT = soundEvent("targeted.reflect", 12f); // implemented
    public static final RegistryObject<SoundEvent> DEFLECT = soundEvent("targeted.deflect", 12f); // implemented
    public static final RegistryObject<SoundEvent> HATCHET_THROW = soundEvent("item.hatchet.throw", 12f); // implemented
    public static final RegistryObject<SoundEvent> BLOODLOSS = soundEvent("entity.bloodloss", 8f); // implemented
    public static final RegistryObject<SoundEvent> FLESH_RIPPING = soundEvent("entity.flesh_ripping", 10f); // implemented
    public static final RegistryObject<SoundEvent> LEADERS_HORN = soundEvent("item.leaders_horn.sound", 256f); // implemented
    public static final RegistryObject<SoundEvent> FURY_DISPEL = soundEvent("ui.fury_dispel", 6f); // implemented
    public static final RegistryObject<SoundEvent> FURY_PROLONG = soundEvent("ui.fury_prolong", 6f); // implemented
    public static final RegistryObject<SoundEvent> BLOODLUST_AMULET = soundEvent("ui.bloodlust_amulet", 6f);; // implemented
    public static final RegistryObject<SoundEvent> EVASION = soundEvent("entity.evasion", 8f); // implemented
    public static final RegistryObject<SoundEvent> REWIND = soundEvent("spell.rewind", 12f); // implemented
    public static final RegistryObject<SoundEvent> ANCIENT_SPARK = soundEvent("item.ancient_spark.use", 8f); // implemented
    public static final RegistryObject<SoundEvent> BOUNCE = soundEvent("spell.bounce", 24f);
    public static final RegistryObject<SoundEvent> WIND = soundEvent("ambient.wind", 32f);
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
