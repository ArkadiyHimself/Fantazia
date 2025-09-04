package net.arkadiyhimself.fantazia.common.api.custom_registry;

import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredSpell<T extends AbstractSpell> extends DeferredHolder<AbstractSpell, T> {

    public static <T extends AbstractSpell> DeferredSpell<T> createSpell(ResourceKey<AbstractSpell> key) {
        return new DeferredSpell<>(key);
    }

    protected DeferredSpell(ResourceKey<AbstractSpell> key) {
        super(key);
    }
}
