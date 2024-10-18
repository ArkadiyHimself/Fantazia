package net.arkadiyhimself.fantazia.data.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public record TalentPredicate(List<ResourceLocation> talents, int amount) {

    public static final Codec<TalentPredicate> CODEC = RecordCodecBuilder.create(talentPredicateInstance -> talentPredicateInstance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("talents", List.of()).forGetter(TalentPredicate::talents),
                    Codec.INT.optionalFieldOf("amount", 0).forGetter(TalentPredicate::amount)
            ).apply(talentPredicateInstance, TalentPredicate::new));

    private boolean matchesLocations(List<ResourceLocation> all) {
        List<ResourceLocation> locations = new java.util.ArrayList<>(List.copyOf(talents));
        locations.removeIf(all::contains);
        return locations.isEmpty();
    }

    public boolean matches(List<ITalent> all) {
        boolean enough = all.size() >= amount;
        List<ResourceLocation> locations = Lists.newArrayList();
        for (ITalent talent : all) locations.add(talent.getID());
        return matchesLocations(locations) && enough;
    }
}
