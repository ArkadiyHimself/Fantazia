package net.arkadiyhimself.fantazia.data.talents;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;

public class TalentTreeData {
    private static final Map<ResourceLocation, IHierarchy<BasicTalent>> ABILITIES = Maps.newHashMap();
    private static final Map<ResourceLocation, IHierarchy<BasicTalent>> STATS_WISDOM = Maps.newHashMap();
    public static ImmutableMap<ResourceLocation, IHierarchy<BasicTalent>> abilities() {
        return ImmutableMap.copyOf(ABILITIES);
    }
    public static ImmutableMap<ResourceLocation, IHierarchy<BasicTalent>> statsWisdom() {
        return ImmutableMap.copyOf(STATS_WISDOM);
    }
    public static void reload() throws TalentDataException {
        Map<ResourceLocation, IHierarchy<ResourceLocation>> abilities = TalentHierarchiesLoad.abilities();
        for (Map.Entry<ResourceLocation, IHierarchy<ResourceLocation>> hierarchyEntry : abilities.entrySet()) {
            IHierarchy<BasicTalent> talentIHierarchy = transform(hierarchyEntry.getValue());
            ABILITIES.put(hierarchyEntry.getKey(), talentIHierarchy);
        }
        Map<ResourceLocation, IHierarchy<ResourceLocation>> statsWisdom = TalentHierarchiesLoad.statsWisdom();
        for (Map.Entry<ResourceLocation, IHierarchy<ResourceLocation>> hierarchyEntry : statsWisdom.entrySet()) {
            IHierarchy<BasicTalent> talentIHierarchy = transform(hierarchyEntry.getValue());
            STATS_WISDOM.put(hierarchyEntry.getKey(), talentIHierarchy);
        }
    }
    public static IHierarchy<BasicTalent> transform(IHierarchy<ResourceLocation> hierarchy) {
        Function<ResourceLocation, BasicTalent> function = (resourceLocation -> {
            BasicTalent talent = TalentLoad.getTalents().get(resourceLocation);
            if (talent == null) throw new TalentDataException("A talent is missing: " + resourceLocation);
            return talent;
        });
        IHierarchy<BasicTalent> talentIHierarchy = hierarchy.transform(function);
        talentIHierarchy.getElements().forEach(talent -> talent.setHierarchy(talentIHierarchy));
        return talentIHierarchy;
    }
}
