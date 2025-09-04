package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public interface FTZEntityTypeTags {

    TagKey<EntityType<?>> RANGED_ATTACK = create("ranged_attack");
    TagKey<EntityType<?>> AERIAL = create("aerial");
    TagKey<EntityType<?>> VALID_WANDERERS_SPIRIT_TARGET = create("valid_wanderers_spirit_target");

    private static TagKey<EntityType<?>> create(String pName) {
        return TagKey.create(Registries.ENTITY_TYPE, Fantazia.location(pName));
    }
}
