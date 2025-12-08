package net.arkadiyhimself.fantazia.data.talent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentHierarchyManager;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TalentTreeData {

    // hierarchy's id || hierarchy itself
    private static final Map<ResourceLocation, TalentHierarchy> LOCATION_TO_HIERARCHY = Maps.newHashMap();

    // tab's id || list of talent hierarchies within the tab
    private static final Map<ResourceLocation, List<TalentHierarchy>> TAB_TO_HIERARCHIES = Maps.newHashMap();

    // talent's id || hierarchy's id
    private static final Map<ResourceLocation, TalentHierarchy> TALENT_TO_HIERARCHY = Maps.newHashMap();

    public static ImmutableMap<ResourceLocation, TalentHierarchy> getLocationToHierarchy() {
        return ImmutableMap.copyOf(LOCATION_TO_HIERARCHY);
    }

    public static ImmutableMap<ResourceLocation, List<TalentHierarchy>> getTabToHierarchies() {
        return ImmutableMap.copyOf(TAB_TO_HIERARCHIES);
    }

    public static ImmutableMap<ResourceLocation, TalentHierarchy> getTalentToHierarchy() {
        return ImmutableMap.copyOf(TALENT_TO_HIERARCHY);
    }

    public static void reload() throws TalentDataException {
        LOCATION_TO_HIERARCHY.clear();
        TAB_TO_HIERARCHIES.clear();
        TALENT_TO_HIERARCHY.clear();

        for (Map.Entry<ResourceLocation, TalentIdHierarchy> hierarchyEntry : ServerTalentHierarchyManager.getAllHierarchies().entrySet())
            LOCATION_TO_HIERARCHY.put(hierarchyEntry.getKey(), transform(hierarchyEntry.getValue()));

        // id of tab || list of hierarchies in the tab
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : ServerTalentHierarchyManager.getTabs().entrySet()) {
            List<TalentHierarchy> iHierarchyList = Lists.newArrayList();
            for (ResourceLocation location : entry.getValue())
                iHierarchyList.add(LOCATION_TO_HIERARCHY.get(location));
            TAB_TO_HIERARCHIES.put(entry.getKey(), iHierarchyList);
        }
    }

    private static TalentHierarchy transform(TalentIdHierarchy hierarchy) {
        Function<ResourceLocation, Talent> function = (id -> {
            Talent talent = ServerTalentManager.getTalent(id);
            if (talent == null) throw new TalentDataException("A talent is missing: " + id);
            return talent;
        });
        IHierarchy<Talent> iHierarchy = hierarchy.hierarchy().transform(function);
        TalentHierarchy talentHierarchy = new TalentHierarchy(iHierarchy, hierarchy.drawLine(), hierarchy.lineColors());
        iHierarchy.getElements().forEach(talent -> TALENT_TO_HIERARCHY.put(talent.id(), talentHierarchy));
        return talentHierarchy;
    }

}
