package net.arkadiyhimself.fantazia.data;

import net.arkadiyhimself.fantazia.advanced.runes.Rune;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class FTZStreamCodecs {

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Rune>> RUNE_HOLDER = holderStreamCodec(FantazicRegistries.Keys.RUNE);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Enchantment>> ENCHANTMENT_HOLDER = holderStreamCodec(Registries.ENCHANTMENT);

    private static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holderStreamCodec(ResourceKey<Registry<T>> key) {
        return ByteBufCodecs.holderRegistry(key);
    }
}
