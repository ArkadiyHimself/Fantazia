package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public interface FTZMobEffectTags {
    TagKey<MobEffect> BARRIER = create("barrier");
    TagKey<MobEffect> INTERRUPT = create("interrupt");
    private static TagKey<MobEffect> create(String pName) {
        return TagKey.create(Registries.MOB_EFFECT, Fantazia.res(pName));
    }
    static boolean hasTag(Holder<MobEffect> mobEffect, TagKey<MobEffect> tagKey) {
        return BuiltInRegistries.MOB_EFFECT.getOrCreateTag(tagKey).stream().toList().contains(mobEffect);
    }
    interface CleanseTags {

        TagKey<MobEffect> MEDIUM = cleanseTag("medium");
        TagKey<MobEffect> POWERFUL = cleanseTag("powerful");
        TagKey<MobEffect> ABSOLUTE = cleanseTag("absolute");

        private static TagKey<MobEffect> cleanseTag(String pName) {
            return TagKey.create(Registries.MOB_EFFECT, Fantazia.res("cleanse/" + pName));
        }
    }
}
