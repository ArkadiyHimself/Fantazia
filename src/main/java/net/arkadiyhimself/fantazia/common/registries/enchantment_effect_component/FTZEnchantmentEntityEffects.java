package net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.enchantment.effects.Combust;
import net.arkadiyhimself.fantazia.common.enchantment.effects.HatchetHeadshot;
import net.arkadiyhimself.fantazia.common.enchantment.effects.HatchetPhasing;
import net.arkadiyhimself.fantazia.common.enchantment.effects.HatchetRicochet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZEnchantmentEntityEffects {

    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> REGISTER =
            DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Fantazia.MODID);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
        register("combust", Combust.CODEC);
        register("hatchet_phasing", HatchetPhasing.CODEC);
        register("hatchet_ricochet", HatchetRicochet.CODEC);
        register("hatchet_headshot", HatchetHeadshot.CODEC);
    }

    private static void register(String name, MapCodec<? extends EnchantmentEntityEffect> codec) {
        REGISTER.register(name, () -> codec);
    }
}
