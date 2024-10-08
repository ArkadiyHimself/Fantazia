package net.arkadiyhimself.fantazia.data.criteritas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.HashSet;
import java.util.List;

public record TalentPredicate(List<ResourceLocation> talents) {
    public static final Codec<TalentPredicate> CODEC = RecordCodecBuilder.create(talentPredicateInstance -> talentPredicateInstance.group(ResourceLocation.CODEC.listOf().optionalFieldOf("talents", List.of()).forGetter(TalentPredicate::talents)).apply(talentPredicateInstance, TalentPredicate::new));
    public boolean matchesLocations(List<ResourceLocation> all) {
        List<ResourceLocation> locations = new java.util.ArrayList<>(List.copyOf(talents));
        locations.removeIf(all::contains);
        return locations.isEmpty();
    }
    public boolean matches(List<BasicTalent> all) {
        List<ResourceLocation> locations = Lists.newArrayList();
        for (BasicTalent talent : all) locations.add(talent.getID());
        return matchesLocations(locations);
    }
    public List<ResourceLocation> talents() {
        return talents;
    }
    public static class Builder {
        private final List<ResourceLocation> talents = Lists.newArrayList();
        public Builder() {}
        public Builder of(String... strings) {
            for (String str : strings) talents.add(ResourceLocation.parse(str));
            return this;
        }
        public TalentPredicate build() {
            return new TalentPredicate(talents);
        }

    }
}
