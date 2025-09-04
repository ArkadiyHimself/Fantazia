package net.arkadiyhimself.fantazia.common.api.custom_registry;

import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredRune<T extends Rune> extends DeferredHolder<Rune, T> {

    public static <T extends Rune> DeferredRune<T> createAura(ResourceKey<Rune> key) {
        return new DeferredRune<>(key);
    }

    protected DeferredRune(ResourceKey<Rune> key) {
        super(key);
    }

}
