package net.arkadiyhimself.fantazia.common.registries;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.api.data_component.HiddenPotentialComponent;
import net.arkadiyhimself.fantazia.common.api.data_component.WisdomTransferComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZDataComponentTypes {
    private FTZDataComponentTypes() {}
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Fantazia.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HiddenPotentialComponent>> HIDDEN_POTENTIAL;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<Rune>>> RUNE;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DASH_LEVEL;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> JEI_AMPLIFIED_ENCHANTMENT;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WisdomTransferComponent>> WISDOM_TRANSFER;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISINTEGRATE;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<Blueprint>>> BLUEPRINT;

    public static void register(IEventBus iEventBus) {
        REGISTER.register(iEventBus);
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return REGISTER.registerComponentType(name, tBuilder -> tBuilder.persistent(codec).networkSynchronized(streamCodec));
    }

    static {
        HIDDEN_POTENTIAL = register("hidden_potential", HiddenPotentialComponent.CODEC, HiddenPotentialComponent.STREAM_CODEC);
        RUNE = register("rune", FantazicRegistries.RUNES.holderByNameCodec(), ByteBufCodecs.holderRegistry(FantazicRegistries.Keys.RUNE));
        DASH_LEVEL = register("dash_level", Codec.INT, ByteBufCodecs.INT);
        JEI_AMPLIFIED_ENCHANTMENT = register("jei_amplified_enchantment", Codec.INT, ByteBufCodecs.INT);
        WISDOM_TRANSFER = register("wisdom_transfer", WisdomTransferComponent.CODEC, WisdomTransferComponent.STREAM_CODEC);
        DISINTEGRATE = register("disintegrate", Codec.BOOL, ByteBufCodecs.BOOL);
        BLUEPRINT = register("blueprint", FantazicRegistries.BLUEPRINTS.holderByNameCodec(), ByteBufCodecs.holderRegistry(FantazicRegistries.Keys.BLUEPRINT));
    }
}
