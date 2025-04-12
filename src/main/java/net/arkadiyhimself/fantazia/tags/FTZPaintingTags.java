package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public interface FTZPaintingTags {

    TagKey<PaintingVariant> FANTAZIC_PLACABLE = create("fantazic_placable");

    private static TagKey<PaintingVariant> create(String pName) {
        return TagKey.create(Registries.PAINTING_VARIANT, Fantazia.res(pName));
    }
}
