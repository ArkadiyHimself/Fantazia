package net.arkadiyhimself.combatimprovement.Registries.Sounds;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.
            create(ForgeRegistries.SOUND_EVENTS, CombatImprovement.MODID);
    public static RegistryObject<SoundEvent> registerSoundEvent(String path, float range) {
        return SOUND_EVENTS.register(path, () -> SoundEvent.createFixedRangeEvent
                (new ResourceLocation(CombatImprovement.MODID, path), range));
    }
    public static final RegistryObject<SoundEvent> HEART_BEAT;

    public static final RegistryObject<SoundEvent> DASH_DEFAULT;
    public static final RegistryObject<SoundEvent> DASH_SECOND;
    public static final RegistryObject<SoundEvent> DASH_FINAL;

    public static final RegistryObject<SoundEvent> DASH1_RECH;
    public static final RegistryObject<SoundEvent> DASH2_RECH;
    public static final RegistryObject<SoundEvent> DASH3_RECH;

    public static final RegistryObject<SoundEvent> FRAG_SWORD_BEGIN;
    public static final RegistryObject<SoundEvent> FRAG_SWORD_LOW;
    public static final RegistryObject<SoundEvent> FRAG_SWORD_MEDIUM;
    public static final RegistryObject<SoundEvent> FRAG_SWORD_HIGH;
    public static final RegistryObject<SoundEvent> FRAG_SWORD_MAXIMUM;

    public static final RegistryObject<SoundEvent> BLOCKED;

    public static final RegistryObject<SoundEvent> ATTACK_STUNNED;

    public static final RegistryObject<SoundEvent> BARRIER_HIT;
    public static final RegistryObject<SoundEvent> BARRIER_BREAK;

    public static final RegistryObject<SoundEvent> RINGING;

    public static final RegistryObject<SoundEvent> DEVOUR;
    public static final RegistryObject<SoundEvent> ENTANGLE;

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

        BLOCKED = registerSoundEvent("blocked", 12f);

        ATTACK_STUNNED = registerSoundEvent("attack_stunned", 8f);

        BARRIER_HIT = registerSoundEvent("barrier_hit", 8f);
        BARRIER_BREAK = registerSoundEvent("barrier_break", 8f);

        RINGING = registerSoundEvent("ringing", 1f);

        DEVOUR = registerSoundEvent("devour", 8f);
        ENTANGLE = registerSoundEvent("entangle", 10f);
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
