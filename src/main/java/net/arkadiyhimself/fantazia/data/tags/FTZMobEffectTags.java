package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public interface FTZMobEffectTags {

    TagKey<MobEffect> BARRIER = create("barrier");
    TagKey<MobEffect> INTERRUPT = create("interrupt");
    TagKey<MobEffect> NO_PARTICLES = create("no_particles");
    TagKey<MobEffect> NO_PARTICLES_ON_ARROWS = create("no_particles_on_arrow");

    private static TagKey<MobEffect> create(String pName) {
        return TagKey.create(Registries.MOB_EFFECT, Fantazia.location(pName));
    }

    interface Cleanse {

        TagKey<MobEffect> MEDIUM = cleanseTag("medium");
        TagKey<MobEffect> POWERFUL = cleanseTag("powerful");
        TagKey<MobEffect> ABSOLUTE = cleanseTag("absolute");

        private static TagKey<MobEffect> cleanseTag(String pName) {
            return TagKey.create(Registries.MOB_EFFECT, Fantazia.location("cleanse/" + pName));
        }
    }
}
