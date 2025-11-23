package net.arkadiyhimself.fantazia.data.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public record SpellPredicate(List<Holder<AbstractSpell>> spells, List<TagPredicate<AbstractSpell>> tagPredicates, PredicateListHandler tagHandler) {

    public static final Codec<SpellPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FantazicRegistries.SPELLS.holderByNameCodec().listOf().optionalFieldOf("spells", List.of()).forGetter(SpellPredicate::spells),
            TagPredicate.codec(FantazicRegistries.Keys.SPELL).listOf().optionalFieldOf("tags", List.of()).forGetter(SpellPredicate::tagPredicates),
            PredicateListHandler.CODEC.optionalFieldOf("handler", PredicateListHandler.AND).forGetter(SpellPredicate::tagHandler)
    ).apply(instance, SpellPredicate::new));

    public boolean test(Holder<AbstractSpell> spell) {
        if (!spells.isEmpty() && !spells.contains(spell)) return false;
        if (tagPredicates.isEmpty()) return true;

        return tagHandler.handle(tagPredicates, spell, TagPredicate::matches);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Holder<AbstractSpell>> spells = Lists.newArrayList();
        private final List<TagPredicate<AbstractSpell>> tagPredicates = Lists.newArrayList();
        private PredicateListHandler predicateListHandler = PredicateListHandler.AND;

        @SafeVarargs
        public final Builder addSpells(Holder<AbstractSpell>... spells) {
            this.spells.addAll(Arrays.stream(spells).toList());
            return this;
        }

        public Builder expectTag(TagKey<AbstractSpell> tagKey) {
            this.tagPredicates.add(TagPredicate.is(tagKey));
            return this;
        }

        public Builder notTag(TagKey<AbstractSpell> tagKey) {
            this.tagPredicates.add(TagPredicate.isNot(tagKey));
            return this;
        }

        public Builder or() {
            this.predicateListHandler = PredicateListHandler.OR;
            return this;
        }

        public SpellPredicate build() {
            return new SpellPredicate(spells, tagPredicates, predicateListHandler);
        }
    }
}
