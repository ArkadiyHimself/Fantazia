package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZPotions {

    private static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(Registries.POTION, Fantazia.MODID);

    public static final DeferredHolder<Potion, Potion> RECOVERY = register("recovery","recovery", FTZMobEffects.RECOVERY, 900, 0);
    public static final DeferredHolder<Potion, Potion> LONG_RECOVERY = register("long_recovery","recovery", FTZMobEffects.RECOVERY, 1800, 0);
    public static final DeferredHolder<Potion, Potion> STRONG_RECOVERY = register("strong_recovery","recovery", FTZMobEffects.RECOVERY, 450, 1);

    public static final DeferredHolder<Potion, Potion> SURGE = register("surge","surge", FTZMobEffects.SURGE, 900, 0);
    public static final DeferredHolder<Potion, Potion> LONG_SURGE = register("long_surge","surge", FTZMobEffects.SURGE, 1800, 0);
    public static final DeferredHolder<Potion, Potion> STRONG_SURGE = register("strong_surge","surge", FTZMobEffects.SURGE, 450, 1);

    public static final DeferredHolder<Potion, Potion> FURY = register("fury","fury", FTZMobEffects.FURY, 900, 0);
    public static final DeferredHolder<Potion, Potion> LONG_FURY = register("long_fury","fury", FTZMobEffects.FURY, 1800, 0);

    public static final DeferredHolder<Potion, Potion> CORROSION = register("corrosion", "corrosion", FTZMobEffects.CORROSION, 1200, 0);
    public static final DeferredHolder<Potion, Potion> LONG_CORROSION = register("long_corrosion", "corrosion", FTZMobEffects.CORROSION, 3000, 0);
    public static final DeferredHolder<Potion, Potion> STRONG_CORROSION = register("strong_corrosion", "corrosion", FTZMobEffects.CORROSION, 600, 1);

    private static DeferredHolder<Potion, Potion> register(String string, String name, Holder<MobEffect> effect, int duration, int amplifier) {
        return REGISTER.register(string, () -> new Potion(name, new MobEffectInstance(effect, duration, amplifier)));
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
