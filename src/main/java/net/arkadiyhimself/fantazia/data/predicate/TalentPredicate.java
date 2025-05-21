package net.arkadiyhimself.fantazia.data.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public record TalentPredicate(List<ResourceLocation> talents, int amount) {

    public static final Codec<TalentPredicate> CODEC = RecordCodecBuilder.create(talentPredicateInstance -> talentPredicateInstance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("talents", List.of()).forGetter(TalentPredicate::talents),
                    Codec.INT.optionalFieldOf("amount", 0).forGetter(TalentPredicate::amount)
            ).apply(talentPredicateInstance, TalentPredicate::new));

    private boolean matchesLocations(List<ResourceLocation> all) {
        List<ResourceLocation> copy = new java.util.ArrayList<>(List.copyOf(talents));
        copy.removeIf(all::contains);
        return copy.isEmpty();
    }

    public boolean matches(List<Talent> all) {
        boolean enough = all.size() >= amount;
        List<ResourceLocation> locations = Lists.newArrayList();
        for (Talent talent : all) locations.add(talent.id());
        return matchesLocations(locations) && enough;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<ResourceLocation> talents = Lists.newArrayList();
        private int amount = 0;

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder addTalents(ResourceLocation... talent) {
            this.talents.addAll(Arrays.stream(talent).toList());
            return this;
        }

        public TalentPredicate build() {
            return new TalentPredicate(talents, amount);
        }
    }
}
