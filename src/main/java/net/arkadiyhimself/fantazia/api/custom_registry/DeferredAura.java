package net.arkadiyhimself.fantazia.api.custom_registry;

import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredAura<T extends Aura> extends DeferredHolder<Aura, T> {

    public static <T extends Aura> DeferredAura<T> createAura(ResourceKey<Aura> key) {
        return new DeferredAura<>(key);
    }

    protected DeferredAura(ResourceKey<Aura> key) {
        super(key);
    }
}
