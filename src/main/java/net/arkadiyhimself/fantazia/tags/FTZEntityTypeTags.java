package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public interface FTZEntityTypeTags {
    TagKey<EntityType<?>> RANGED_ATTACK = create("ranged_attack");
    TagKey<EntityType<?>> AERIAL = create("aerial");

    private static TagKey<EntityType<?>> create(String pName) {
        return TagKey.create(Registries.ENTITY_TYPE, Fantazia.res(pName));
    }
}
