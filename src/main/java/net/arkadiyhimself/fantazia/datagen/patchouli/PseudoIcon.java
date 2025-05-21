package net.arkadiyhimself.fantazia.datagen.patchouli;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;

public record PseudoIcon(Optional<ResourceLocation> id, Optional<Item> item) {

    public ResourceLocation getId() throws IllegalStateException {
        if (item.isPresent()) {
            return BuiltInRegistries.ITEM.getKey(item.get());
        } else {
            if (id.isPresent()) return id.get();
            else throw new IllegalStateException("Could not get icon!");
        }
    }

    public static PseudoIcon fromId(ResourceLocation id) {
        if (id.getPath().endsWith(".png")) return new PseudoIcon(Optional.of(id), Optional.empty());
        else {
            if (!BuiltInRegistries.ITEM.containsKey(id)) throw new IllegalStateException("Could not get icon!");
            Item item = BuiltInRegistries.ITEM.get(id);
            return new PseudoIcon(Optional.empty(), Optional.of(item));
        }
    }

    public static PseudoIcon fromItem(Item item) {
        return new PseudoIcon(Optional.empty(), Optional.of(item));
    }
}
