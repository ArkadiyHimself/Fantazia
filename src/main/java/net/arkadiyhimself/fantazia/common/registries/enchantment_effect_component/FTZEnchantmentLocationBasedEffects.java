package net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.enchantment.effects.Combust;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZEnchantmentLocationBasedEffects {

    public static final DeferredRegister<MapCodec<? extends EnchantmentLocationBasedEffect>> REGISTER =
            DeferredRegister.create(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, Fantazia.MODID);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static void register(String name, MapCodec<? extends EnchantmentLocationBasedEffect> codec) {
        REGISTER.register(name, () -> codec);
    }

    static {
        register("combust", Combust.CODEC);
    }
}
