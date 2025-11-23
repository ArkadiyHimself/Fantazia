package net.arkadiyhimself.fantazia.util.simpleobjects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.BiPredicate;

public record RegistryObjectList<T>(List<T> objects, List<TagKey<T>> tagList) {

    public static <M> Codec<RegistryObjectList<M>> codec(ResourceKey<Registry<M>> key, String objectList, String tagList) {
        Registry<M> registry =
                (Registry<M>) BuiltInRegistries.REGISTRY.get(key.location());
        if (registry == null) return null;
        return RecordCodecBuilder.create(instance -> instance.group(
                registry.byNameCodec().listOf().optionalFieldOf(objectList, List.of()).forGetter(RegistryObjectList::objects),
                TagKey.codec(key).listOf().optionalFieldOf(tagList, List.of()).forGetter(RegistryObjectList::tagList)
                ).apply(instance, RegistryObjectList::new)
        );
    }

    public boolean isEmpty() {
        return objects.isEmpty() && tagList.isEmpty();
    }

    public boolean contains(T t, BiPredicate<T, TagKey<T>> isTag) {
        if (objects.contains(t)) return true;
        for (TagKey<T> tag : tagList)
            if (isTag.test(t, tag)) return true;
        return false;
    }

    public static <M> Builder<M> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private final List<T> objects = Lists.newArrayList();
        private final List<TagKey<T>> tags = Lists.newArrayList();

        @SafeVarargs
        public final Builder<T> addObjects(T... ts) {
            objects.addAll(List.of(ts));
            return this;
        }

        @SafeVarargs
        public final Builder<T> addTags(TagKey<T>... tagKeys) {
            tags.addAll(List.of(tagKeys));
            return this;
        }

        public RegistryObjectList<T> build() {
            return new RegistryObjectList<>(objects, tags);
        }
    }
}
