package net.arkadiyhimself.fantazia.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.runes.Rune;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZDataComponentTypes {
    private FTZDataComponentTypes() {}
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Fantazia.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HiddenPotentialHolder>> HIDDEN_POTENTIAL = REGISTER.register("hidden_potential", () -> DataComponentType.<HiddenPotentialHolder>builder().persistent(HiddenPotentialHolder.CODEC).networkSynchronized(HiddenPotentialHolder.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<Rune>>> RUNE = REGISTER.register("rune", () -> DataComponentType.<Holder<Rune>>builder().persistent(FantazicRegistries.RUNES.holderByNameCodec()).networkSynchronized(ByteBufCodecs.holderRegistry(FantazicRegistries.Keys.RUNE)).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DASH_LEVEL = REGISTER.register("dash_level", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> JEI_AMPLIFIED_ENCHANTMENT = REGISTER.register("jei_amplified_enchantment", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static void register(IEventBus iEventBus) {
        REGISTER.register(iEventBus);
    }
}
