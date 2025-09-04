package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public interface FTZPaintingVariantTags {

    TagKey<PaintingVariant> FANTAZIC_PLACAEBLE = create("fantazic_placeable");

    private static TagKey<PaintingVariant> create(String pName) {
        return TagKey.create(Registries.PAINTING_VARIANT, Fantazia.location(pName));
    }
}
