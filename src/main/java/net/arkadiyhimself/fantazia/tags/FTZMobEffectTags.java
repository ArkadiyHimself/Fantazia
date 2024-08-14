package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

public interface FTZMobEffectTags {
    TagKey<MobEffect> BARRIER = create("barrier");
    TagKey<MobEffect> INTERRUPT = create("interrupt");
    private static TagKey<MobEffect> create(String pName) {
        return TagKey.create(Registries.MOB_EFFECT, Fantazia.res(pName));
    }
    static boolean hasTag(MobEffect mobEffect, TagKey<MobEffect> tagKey) {
        ITagManager<MobEffect> tagManager = ForgeRegistries.MOB_EFFECTS.tags();
        if (tagManager == null || !tagManager.getTagNames().toList().contains(tagKey)) return false;
        return tagManager.getTag(tagKey).contains(mobEffect);
    }
    final class CleanseTags {
        public static final TagKey<MobEffect> MEDIUM = cleanseTag("medium");
        public static final TagKey<MobEffect> POWERFUL = cleanseTag("powerful");
        public static final TagKey<MobEffect> ABSOLUTE = cleanseTag("absolute");
        private static TagKey<MobEffect> cleanseTag(String pName) {
            return TagKey.create(Registries.MOB_EFFECT, Fantazia.res("cleanse/" + pName));
        }
    }
}
