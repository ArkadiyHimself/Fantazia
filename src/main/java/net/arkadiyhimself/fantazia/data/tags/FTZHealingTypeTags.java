package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.tags.TagKey;

public interface FTZHealingTypeTags {

    TagKey<HealingType> REGEN = create("regen");
    TagKey<HealingType> MOB_EFFECT = create("mob_effect");
    TagKey<HealingType> BYPASSES_INVULNERABILITY = create("bypasses_invulnerability");
    TagKey<HealingType> SCALES_FROM_SATURATION = create("scales_from_saturation");
    TagKey<HealingType> NOT_CANCELLABLE = create("not_cancellable");
    TagKey<HealingType> SELF = create("self");
    TagKey<HealingType> CONSENSUAL = create("consensual");
    TagKey<HealingType> UNHOLY = create("unholy");
    private static TagKey<HealingType> create(String pName) {
        return TagKey.create(FantazicRegistries.Keys.HEALING_TYPE, Fantazia.location(pName));
    }
}
