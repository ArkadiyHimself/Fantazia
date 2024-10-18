package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface FTZDamageTypeTags {

    TagKey<DamageType> NO_HURT_SOUND = create("no_hurt_sound");
    TagKey<DamageType> NOT_SHAKING_SCREEN = create("not_shaking_screen");
    TagKey<DamageType> NOT_TURNING_RED = create("not_turning_red");
    TagKey<DamageType> NON_LETHAL = create("non_lethal");
    TagKey<DamageType> PIERCES_BARRIER = create("pierces_barrier");
    TagKey<DamageType> NOT_STOPPING_DASH = create("not_stopping_dash");
    TagKey<DamageType> ELECTRIC = create("electric");

    private static TagKey<DamageType> create(String pName) {
        return TagKey.create(Registries.DAMAGE_TYPE, Fantazia.res(pName));
    }
}
