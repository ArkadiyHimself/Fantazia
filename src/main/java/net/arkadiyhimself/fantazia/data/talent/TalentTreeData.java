package net.arkadiyhimself.fantazia.data.talent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentHierarchyManager;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentManager;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TalentTreeData {

    // hierarchy's id || hierarchy itself
    private static final Map<ResourceLocation, IHierarchy<ITalent>> LOCATION_TO_HIERARCHY = Maps.newHashMap();

    // tab's id || list of talent hierarchies within the tab
    private static final Map<ResourceLocation, List<IHierarchy<ITalent>>> TAB_TO_HIERARCHIES = Maps.newHashMap();

    // talent's id || hierarchy's id
    private static final Map<ResourceLocation, IHierarchy<ITalent>> TALENT_TO_HIERARCHY = Maps.newHashMap();

    public static ImmutableMap<ResourceLocation, IHierarchy<ITalent>> getLocationToHierarchy() {
        return ImmutableMap.copyOf(LOCATION_TO_HIERARCHY);
    }

    public static ImmutableMap<ResourceLocation, List<IHierarchy<ITalent>>> getTabToHierarchies() {
        return ImmutableMap.copyOf(TAB_TO_HIERARCHIES);
    }

    public static ImmutableMap<ResourceLocation, IHierarchy<ITalent>> getTalentToHierarchy() {
        return ImmutableMap.copyOf(TALENT_TO_HIERARCHY);
    }

    public static void reload() throws TalentDataException {
        LOCATION_TO_HIERARCHY.clear();
        TAB_TO_HIERARCHIES.clear();
        TALENT_TO_HIERARCHY.clear();

        for (Map.Entry<ResourceLocation, IHierarchy<ResourceLocation>> hierarchyEntry : TalentHierarchyManager.getAllHierarchies().entrySet()) LOCATION_TO_HIERARCHY.put(hierarchyEntry.getKey(), transform(hierarchyEntry.getValue()));

        // id of tab || list of hierarchies in the tab
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : TalentHierarchyManager.getTabs().entrySet()) {
            List<IHierarchy<ITalent>> iHierarchyList = Lists.newArrayList();
            for (ResourceLocation location : entry.getValue()) iHierarchyList.add(LOCATION_TO_HIERARCHY.get(location));
            TAB_TO_HIERARCHIES.put(entry.getKey(), iHierarchyList);
        }
    }

    private static IHierarchy<ITalent> transform(IHierarchy<ResourceLocation> hierarchy) {
        Function<ResourceLocation, ITalent> function = (resourceLocation -> {
            ITalent talent = TalentManager.getTalents().get(resourceLocation);
            if (talent == null) throw new TalentDataException("A talent is missing: " + resourceLocation);
            return talent;
        });
        IHierarchy<ITalent> talentIHierarchy = hierarchy.transform(function);
        talentIHierarchy.getElements().forEach(talent -> TALENT_TO_HIERARCHY.put(talent.getID(), talentIHierarchy));
        return talentIHierarchy;
    }

}
