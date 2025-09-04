package net.arkadiyhimself.fantazia.data.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;

public record DynamicResourceLocation(ResourceLocation regular) {

    public ResourceLocation fantazia() {
        return Fantazia.changeNamespace(regular);
    }
}
