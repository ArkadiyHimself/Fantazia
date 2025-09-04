package net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZEnchantmentValueEffects {

    public static final DeferredRegister<MapCodec<? extends EnchantmentValueEffect>> REGISTER =
            DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE, Fantazia.MODID);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
