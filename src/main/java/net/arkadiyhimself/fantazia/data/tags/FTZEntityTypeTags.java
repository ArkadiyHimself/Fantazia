package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public interface FTZEntityTypeTags {

    TagKey<EntityType<?>> RANGED_ATTACK = create("ranged_attack");
    TagKey<EntityType<?>> AERIAL = create("aerial");
    TagKey<EntityType<?>> VALID_WANDERERS_SPIRIT_TARGET = create("valid_wanderers_spirit_target");
    TagKey<EntityType<?>> IS_FROM_A_RECHARGEABLE_TOOL = create("is_from_a_rechargeable_tool");

    private static TagKey<EntityType<?>> create(String pName) {
        return TagKey.create(Registries.ENTITY_TYPE, Fantazia.location(pName));
    }
}
