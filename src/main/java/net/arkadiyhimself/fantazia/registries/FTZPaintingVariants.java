package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public interface FTZPaintingVariants {
    ResourceKey<PaintingVariant> FANTAZIA = register("fantazia");
    ResourceKey<PaintingVariant> KAPITON = register("kapiton");

    private static ResourceKey<PaintingVariant> register(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, Fantazia.res(name));
    }
}
