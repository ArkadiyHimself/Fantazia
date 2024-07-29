package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class FTZSoundEvents extends FTZRegistry<SoundEvent> {
    private static final FTZSoundEvents INSTANCE = new FTZSoundEvents();
    @ObjectHolder(value = Fantazia.MODID + ":fury_heartbeat1", registryName = "sound_event")
    public static SoundEvent HEART_BEAT1 = null;
    @ObjectHolder(value = Fantazia.MODID + ":fury_heartbeat2", registryName = "sound_event")
    public static SoundEvent HEART_BEAT2 = null;
    @ObjectHolder(value = Fantazia.MODID + ":dash.dash_default", registryName = "sound_event")
    public static SoundEvent DASH_DEFAULT = null;
    @ObjectHolder(value = Fantazia.MODID + ":dash.dash_second", registryName = "sound_event")
    public static SoundEvent DASH_SECOND = null;
    @ObjectHolder(value = Fantazia.MODID + ":dash.dash_final", registryName = "sound_event")
    public static SoundEvent DASH_FINAL = null;
    @ObjectHolder(value = Fantazia.MODID + ":dash.dash1_recharge", registryName = "sound_event")
    public static SoundEvent DASH1_RECH = null;
    @ObjectHolder(value = Fantazia.MODID + ":dash.dash2_recharge", registryName = "sound_event")
    public static SoundEvent DASH2_RECH = null;
    @ObjectHolder(value = Fantazia.MODID + ":dash.dash3_recharge", registryName = "sound_event")
    public static SoundEvent DASH3_RECH = null;
    @ObjectHolder(value = Fantazia.MODID + ":fragile.sword.begin", registryName = "sound_event")
    public static SoundEvent FRAG_SWORD_BEGIN = null;
    @ObjectHolder(value = Fantazia.MODID + ":fragile.sword.low", registryName = "sound_event")
    public static SoundEvent FRAG_SWORD_LOW = null;
    @ObjectHolder(value = Fantazia.MODID + ":fragile.sword.medium", registryName = "sound_event")
    public static SoundEvent FRAG_SWORD_MEDIUM = null;
    @ObjectHolder(value = Fantazia.MODID + ":fragile.sword.high", registryName = "sound_event")
    public static SoundEvent FRAG_SWORD_HIGH = null;
    @ObjectHolder(value = Fantazia.MODID + ":fragile.sword.maximum", registryName = "sound_event")
    public static SoundEvent FRAG_SWORD_MAXIMUM = null;
    @ObjectHolder(value = Fantazia.MODID + ":fragile.sword.unleashed", registryName = "sound_event")
    public static SoundEvent FRAG_SWORD_UNLEASHED = null;
    @ObjectHolder(value = Fantazia.MODID + ":blocked", registryName = "sound_event")
    public static SoundEvent BLOCKED = null;
    @ObjectHolder(value = Fantazia.MODID + ":attack_stunned", registryName = "sound_event")
    public static SoundEvent ATTACK_STUNNED = null;
    @ObjectHolder(value = Fantazia.MODID + ":barrier_hit", registryName = "sound_event")
    public static SoundEvent BARRIER_HIT = null;
    @ObjectHolder(value = Fantazia.MODID + ":barrier_break", registryName = "sound_event")
    public static SoundEvent BARRIER_BREAK = null;
    @ObjectHolder(value = Fantazia.MODID + ":ringing", registryName = "sound_event")
    public static SoundEvent RINGING = null;
    @ObjectHolder(value = Fantazia.MODID + ":devour", registryName = "sound_event")
    public static SoundEvent DEVOUR = null;
    @ObjectHolder(value = Fantazia.MODID + ":entangle", registryName = "sound_event")
    public static SoundEvent ENTANGLE = null;
    @ObjectHolder(value = Fantazia.MODID + ":doomed", registryName = "sound_event")
    public static SoundEvent DOOMED = null;
    @ObjectHolder(value = Fantazia.MODID + ":undoomed", registryName = "sound_event")
    public static SoundEvent UNDOOMED = null;
    @ObjectHolder(value = Fantazia.MODID + ":fallen_breath", registryName = "sound_event")
    public static SoundEvent FALLEN_BREATH = null;
    @ObjectHolder(value = Fantazia.MODID + ":whisper", registryName = "sound_event")
    public static SoundEvent WHISPER = null;
    @ObjectHolder(value = Fantazia.MODID + ":denied", registryName = "sound_event")
    public static SoundEvent DENIED = null;
    @ObjectHolder(value = Fantazia.MODID + ":mystic_mirror", registryName = "sound_event")
    public static SoundEvent MYSTIC_MIRROR = null;
    @ObjectHolder(value = Fantazia.MODID + ":reflect", registryName = "sound_event")
    public static SoundEvent REFLECT = null;
    @ObjectHolder(value = Fantazia.MODID + ":deflect", registryName = "sound_event")
    public static SoundEvent DEFLECT = null;
    @ObjectHolder(value = Fantazia.MODID + ":hatchet_throw", registryName = "sound_event")
    public static SoundEvent HATCHET_THROW = null;
    @ObjectHolder(value = Fantazia.MODID + ":bloodloss", registryName = "sound_event")
    public static SoundEvent BLOODLOSS = null;
    @ObjectHolder(value = Fantazia.MODID + ":flesh_ripping", registryName = "sound_event")
    public static SoundEvent FLESH_RIPPING = null;
    @ObjectHolder(value = Fantazia.MODID + ":leaders_horn", registryName = "sound_event")
    public static SoundEvent LEADERS_HORN = null;
    @ObjectHolder(value = Fantazia.MODID + ":fury_dispel", registryName = "sound_event")
    public static SoundEvent FURY_DISPEL = null;
    @ObjectHolder(value = Fantazia.MODID + ":fury_prolong", registryName = "sound_event")
    public static SoundEvent FURY_PROLONG = null;
    @ObjectHolder(value = Fantazia.MODID + ":bloodlust_amulet", registryName = "sound_event")
    public static SoundEvent BLOODLUST_AMULET = null;


    private FTZSoundEvents() {
        super(ForgeRegistries.SOUND_EVENTS);

        this.soundEvent("fury_heartbeat1", 1f);
        this.soundEvent("fury_heartbeat2", 1f);

        this.soundEvent("dash.dash_default", 8f);
        this.soundEvent("dash.dash_second", 12f);
        this.soundEvent("dash.dash_final", 16f);

        this.soundEvent("dash.dash1_recharge", 1f);
        this.soundEvent("dash.dash2_recharge", 1f);
        this.soundEvent("dash.dash3_recharge", 1f);

        this.soundEvent("fragile.sword.begin", 8f);
        this.soundEvent("fragile.sword.low", 8f);
        this.soundEvent("fragile.sword.medium", 8f);
        this.soundEvent("fragile.sword.high", 8f);
        this.soundEvent("fragile.sword.maximum", 12f);
        this.soundEvent("fragile.sword.unleashed", 16f);

        this.soundEvent("blocked", 12f);
        this.soundEvent("attack_stunned", 8f);

        this.soundEvent("barrier_hit", 8f);
        this.soundEvent("barrier_break", 8f);

        this.soundEvent("ringing", 1f);

        this.soundEvent("devour", 8f);
        this.soundEvent("entangle", 10f);

        this.soundEvent("doomed", 16f);
        this.soundEvent("undoomed", 16f);
        this.soundEvent("fallen_breath", 12f);
        this.soundEvent("whisper", 16f);

        this.soundEvent("denied", 16f);

        this.soundEvent("mystic_mirror", 12f);
        this.soundEvent("reflect", 12f);
        this.soundEvent("deflect", 12f);

        this.soundEvent("hatchet_throw", 12f);

        this.soundEvent("bloodloss", 8f);
        this.soundEvent("flesh_ripping", 10f);

        this.soundEvent("leaders_horn", 256f);

        this.soundEvent("fury_dispel", 6f);
        this.soundEvent("fury_prolong", 6f);
        this.soundEvent("bloodlust_amulet", 6f);
    }
    private void soundEvent(String path, float range) {
        this.register(path, () -> SoundEvent.createFixedRangeEvent(Fantazia.res(path), range));
    }
}
