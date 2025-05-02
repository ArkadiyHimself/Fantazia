package net.arkadiyhimself.fantazia.api.custom_registry;

import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredAura<T extends BasicAura> extends DeferredHolder<BasicAura, T> {

    public static <T extends BasicAura> DeferredAura<T> createAura(ResourceKey<BasicAura> key) {
        return new DeferredAura<>(key);
    }

    protected DeferredAura(ResourceKey<BasicAura> key) {
        super(key);
    }
}
