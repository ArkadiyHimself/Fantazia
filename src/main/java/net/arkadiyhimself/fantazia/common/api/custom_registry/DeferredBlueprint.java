package net.arkadiyhimself.fantazia.common.api.custom_registry;

import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredBlueprint<T extends Blueprint> extends DeferredHolder<Blueprint, T> {

    public static <T extends Blueprint> DeferredBlueprint<T> createBlueprint(ResourceKey<Blueprint> key) {
        return new DeferredBlueprint<>(key);
    }

    protected DeferredBlueprint(ResourceKey<Blueprint> key) {
        super(key);
    }

}
