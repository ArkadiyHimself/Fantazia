package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public interface FTZPaintingVariants {

    ResourceKey<PaintingVariant> FANTAZIA = register("fantazia");
    ResourceKey<PaintingVariant> KAPITON = register("kapiton");
    ResourceKey<PaintingVariant> MICATLANGELO = register("micatlangelo");
    ResourceKey<PaintingVariant> JAMES_CATFIELD = register("james_catfield");

    private static ResourceKey<PaintingVariant> register(String name) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, Fantazia.location(name));
    }

    static void bootStrap(BootstrapContext<PaintingVariant> context) {
        context.register(FTZPaintingVariants.FANTAZIA, new PaintingVariant(2,2, Fantazia.location("fantazia")));
        context.register(FTZPaintingVariants.KAPITON, new PaintingVariant(4,4, Fantazia.location("kapiton")));
        context.register(FTZPaintingVariants.MICATLANGELO, new PaintingVariant(5,3, Fantazia.location("micatlangelo")));
        context.register(FTZPaintingVariants.JAMES_CATFIELD, new PaintingVariant(4,3, Fantazia.location("james_catfield")));
    }
}
