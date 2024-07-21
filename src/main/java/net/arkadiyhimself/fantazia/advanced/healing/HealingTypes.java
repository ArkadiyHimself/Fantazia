package net.arkadiyhimself.fantazia.advanced.healing;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registry.ParticleRegistry;

public class HealingTypes {
    public static final HealingType VANILLA = new HealingType(0f, Fantazia.res("vanilla"));
    public static final HealingType GENERIC = new HealingType(0f, Fantazia.res("generic"));
    public static final HealingType GENERIC_MOBEFFECT = new HealingType(0f, Fantazia.res("generic_mobeffect"));
    public static final HealingType LIFESTEAL = new HealingType(0f, Fantazia.res("lifesteal"), HealingTag.SCALES_FROM_SATURATION, HealingTag.SELF, HealingTag.UNHOLY)
            .setRegParticles(ParticleRegistry.LIFESTEAL);
    public static final HealingType REGEN_EFFECT = new HealingType(0f, Fantazia.res("regen_effect"), HealingTag.SCALES_FROM_SATURATION, HealingTag.REGEN).setRegParticles(ParticleRegistry.REGENERATION);
    public static final HealingType REGEN_MOBEFFECT = new HealingType(0f, Fantazia.res("regen_mobeffect"), HealingTag.SCALES_FROM_SATURATION, HealingTag.REGEN);
    public static final HealingType REGEN_NATURAL = new HealingType(0.005f, Fantazia.res("regen_natural"), HealingTag.SCALES_FROM_SATURATION, HealingTag.SELF, HealingTag.REGEN);
    public static final HealingType DEVOUR = new HealingType(0f, Fantazia.res("devour"), HealingTag.SCALES_FROM_SATURATION, HealingTag.UNHOLY);
}
