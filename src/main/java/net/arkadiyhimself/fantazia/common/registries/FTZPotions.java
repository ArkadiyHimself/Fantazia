package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZPotions {

    private static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(Registries.POTION, Fantazia.MODID);

    public static final DeferredHolder<Potion, Potion> RECOVERY = registerBasic(FTZMobEffects.RECOVERY);
    public static final DeferredHolder<Potion, Potion> LONG_RECOVERY = registerLong(FTZMobEffects.RECOVERY);
    public static final DeferredHolder<Potion, Potion> STRONG_RECOVERY = registerStrong(FTZMobEffects.RECOVERY);

    public static final DeferredHolder<Potion, Potion> SURGE = registerBasic(FTZMobEffects.SURGE);
    public static final DeferredHolder<Potion, Potion> LONG_SURGE = registerLong(FTZMobEffects.SURGE);
    public static final DeferredHolder<Potion, Potion> STRONG_SURGE = registerStrong(FTZMobEffects.SURGE);

    public static final DeferredHolder<Potion, Potion> FURY = registerBasic(FTZMobEffects.FURY);
    public static final DeferredHolder<Potion, Potion> LONG_FURY = registerLong(FTZMobEffects.FURY);

    public static final DeferredHolder<Potion, Potion> CORROSION = registerBasic(FTZMobEffects.CORROSION, 1200);
    public static final DeferredHolder<Potion, Potion> LONG_CORROSION = registerLong(FTZMobEffects.CORROSION, 3000);
    public static final DeferredHolder<Potion, Potion> STRONG_CORROSION = registerStrong(FTZMobEffects.CORROSION, 600);

    public static final DeferredHolder<Potion, Potion> CHAINED = registerBasic(FTZMobEffects.CHAINED);
    public static final DeferredHolder<Potion, Potion> LONG_CHAINED = registerLong(FTZMobEffects.CHAINED);

    private static DeferredHolder<Potion, Potion> registerBasic(DeferredHolder<MobEffect, ? extends MobEffect> effect, int duration) {
        String name = effect.getId().getPath();
        return REGISTER.register(name, () -> new Potion(name, new MobEffectInstance(effect, duration)));
    }

    private static DeferredHolder<Potion, Potion> registerBasic(DeferredHolder<MobEffect, ? extends MobEffect> effect) {
        return registerBasic(effect, 900);
    }

    private static DeferredHolder<Potion, Potion> registerLong(DeferredHolder<MobEffect, ? extends MobEffect> effect, int duration) {
        String name = "long_" + effect.getId().getPath();
        return REGISTER.register(name, () -> new Potion(name, new MobEffectInstance(effect, duration)));
    }

    private static DeferredHolder<Potion, Potion> registerLong(DeferredHolder<MobEffect, ? extends MobEffect> effect) {
        return registerLong(effect, 1800);
    }

    private static DeferredHolder<Potion, Potion> registerStrong(DeferredHolder<MobEffect, ? extends MobEffect> effect, int duration) {
        String name = "strong_" +  effect.getId().getPath();
        return REGISTER.register(name, () -> new Potion(name, new MobEffectInstance(effect, duration, 1)));
    }

    private static DeferredHolder<Potion, Potion> registerStrong(DeferredHolder<MobEffect, ? extends MobEffect> effect) {
        return registerStrong(effect, 450);
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
