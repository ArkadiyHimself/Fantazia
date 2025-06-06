package net.arkadiyhimself.fantazia.data.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.advanced.runes.Rune;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.CustomCriteriaHolder;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record PossessedRunePredicate(List<Holder<Rune>> runeList, int runes, Optional<TagKey<Rune>> tagKey, int tagAmount) {

    public static final Codec<PossessedRunePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FantazicRegistries.RUNES.holderByNameCodec().listOf().optionalFieldOf("rune_list", Lists.newArrayList()).forGetter(PossessedRunePredicate::runeList),
            Codec.INT.optionalFieldOf("runes",0).forGetter(PossessedRunePredicate::runes),
            TagKey.codec(FantazicRegistries.Keys.RUNE).optionalFieldOf("tag").forGetter(PossessedRunePredicate::tagKey),
            Codec.INT.optionalFieldOf("tag_amount", 0).forGetter(PossessedRunePredicate::tagAmount)
    ).apply(instance, PossessedRunePredicate::new));

    public boolean matches(@NotNull CustomCriteriaHolder holder) {
        List<Holder<Rune>> obtainedRunes = holder.getObtainedRunes();
        List<Holder<Rune>> required = new ArrayList<>(runeList);

        if (!required.isEmpty()) {
            required.removeIf(obtainedRunes::contains);
            if (!required.isEmpty()) return false;
        }

        if (runes > obtainedRunes.size()) return false;

        if (tagKey.isPresent()) {
            TagKey<Rune> tag = tagKey.get();
            List<Holder<Rune>> tagged = Lists.newArrayList();
            for (Holder<Rune> obtained : obtainedRunes) if (obtained.is(tag)) tagged.add(obtained);
            if (tagAmount > tagged.size()) return false;
        }

        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<Holder<Rune>> runeList = Lists.newArrayList();
        private int runes = 0;
        private TagKey<Rune> tagKey = null;
        private int tagAmount = 0;

        @SafeVarargs
        public final Builder addRunes(Holder<Rune>... holders) {
            runeList.addAll(Arrays.asList(holders));
            return this;
        }

        public Builder runes(int runes) {
            this.runes = runes;
            return this;
        }

        public Builder tagKey(TagKey<Rune> tagKey) {
            this.tagKey = tagKey;
            return this;
        }

        public Builder tagAmount(int tagAmount) {
            this.tagAmount = tagAmount;
            return this;
        }

        public PossessedRunePredicate build() {
            return new PossessedRunePredicate(runeList, runes, Optional.ofNullable(tagKey), tagAmount);
        }
    }
}
