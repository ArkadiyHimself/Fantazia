package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public interface FTZPaintingVariants {
    ResourceKey<PaintingVariant> FANTAZIA = register("fantazia");
    ResourceKey<PaintingVariant> KAPITON = register("kapiton");
    ResourceKey<PaintingVariant> MICATLANGELO = register("micatlangelo");

    private static ResourceKey<PaintingVariant> register(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, Fantazia.res(name));
    }

    static void bootStrap(BootstrapContext<PaintingVariant> context) {
        context.register(FTZPaintingVariants.FANTAZIA, new PaintingVariant(2,2, Fantazia.res("fantazia")));
        context.register(FTZPaintingVariants.KAPITON, new PaintingVariant(4,4, Fantazia.res("kapiton")));
        context.register(FTZPaintingVariants.MICATLANGELO, new PaintingVariant(5,3, Fantazia.res("micatlangelo")));
    }
}
