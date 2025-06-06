package net.arkadiyhimself.fantazia.data.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.FTZCodecs;
import net.arkadiyhimself.fantazia.data.PredicateListHandler;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public record DamageTypePredicate(List<ResourceKey<DamageType>> damageTypes, List<TagPredicate<DamageType>> tagPredicates, PredicateListHandler tagsHandler) {

    public static final Codec<DamageTypePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FTZCodecs.DAMAGE_TYPE.listOf().optionalFieldOf("damage_types", Lists.newArrayList()).forGetter(DamageTypePredicate::damageTypes),
            TagPredicate.codec(Registries.DAMAGE_TYPE).listOf().optionalFieldOf("tags", Lists.newArrayList()).forGetter(DamageTypePredicate::tagPredicates),
            PredicateListHandler.CODEC.optionalFieldOf("tags_handler", PredicateListHandler.AND).forGetter(DamageTypePredicate::tagsHandler)
    ).apply(instance, DamageTypePredicate::new));

    public boolean matches(Holder<DamageType> damageType) {
        if (!damageTypes.isEmpty() && !damageTypes.contains(damageType.getKey())) return false;
        if (tagPredicates.isEmpty()) return true;

        return tagsHandler.handle(tagPredicates, damageType, TagPredicate::matches);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<ResourceKey<DamageType>> damageTypes = Lists.newArrayList();
        private final List<TagPredicate<DamageType>> tagPredicates = Lists.newArrayList();
        private PredicateListHandler handler = PredicateListHandler.AND;

        @SafeVarargs
        public final Builder addDamageTypes(ResourceKey<DamageType>... keys) {
            this.damageTypes.addAll(Arrays.stream(keys).toList());
            return this;
        }

        @SafeVarargs
        public final Builder addTagPredicates(TagPredicate<DamageType>... predicates) {
            this.tagPredicates.addAll(Arrays.stream(predicates).toList());
            return this;
        }

        @SafeVarargs
        public final Builder addTagPredicates(TagKey<DamageType>... tagKeys) {
            for (TagKey<DamageType> tagKey : tagKeys) this.tagPredicates.add(TagPredicate.is(tagKey));
            return this;
        }

        public Builder or() {
            this.handler = PredicateListHandler.OR;
            return this;
        }

        public DamageTypePredicate build() {
            return new DamageTypePredicate(damageTypes, tagPredicates, handler);
        }
    }
}
