package net.arkadiyhimself.fantazia.data.talent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentHierarchyManager;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentManager;
import net.arkadiyhimself.fantazia.data.talent.types.BasicTalent;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TalentTreeData {
    // hierarchy's id || hierarchy itself
    private static final Map<ResourceLocation, IHierarchy<BasicTalent>> ALL_HIERARCHIES = Maps.newHashMap();
    // tab's id || list of talent hierarchies within the tab
    private static final Map<ResourceLocation, List<IHierarchy<BasicTalent>>> TAB_HIERARCHIES = Maps.newHashMap();
    public static ImmutableMap<ResourceLocation, IHierarchy<BasicTalent>> getAllHierarchies() {
        return ImmutableMap.copyOf(ALL_HIERARCHIES);
    }
    public static ImmutableMap<ResourceLocation, List<IHierarchy<BasicTalent>>> getTabHierarchies() {
        return ImmutableMap.copyOf(TAB_HIERARCHIES);
    }
    public static void reload() throws TalentDataException {
        ALL_HIERARCHIES.clear();
        TAB_HIERARCHIES.clear();

        for (Map.Entry<ResourceLocation, IHierarchy<ResourceLocation>> hierarchyEntry : TalentHierarchyManager.getAllHierarchies().entrySet())
            ALL_HIERARCHIES.put(hierarchyEntry.getKey(), transform(hierarchyEntry.getValue()));


        // id of tab || list of hierarchies in the tab
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : TalentHierarchyManager.getTabs().entrySet()) {
            List<IHierarchy<BasicTalent>> iHierarchyList = Lists.newArrayList();
            for (ResourceLocation location : entry.getValue()) iHierarchyList.add(ALL_HIERARCHIES.get(location));
            TAB_HIERARCHIES.put(entry.getKey(), iHierarchyList);
        }
    }
    private static IHierarchy<BasicTalent> transform(IHierarchy<ResourceLocation> hierarchy) {
        Function<ResourceLocation, BasicTalent> function = (resourceLocation -> {
            BasicTalent talent = TalentManager.getTalents().get(resourceLocation);
            if (talent == null) throw new TalentDataException("A talent is missing: " + resourceLocation);
            return talent;
        });
        IHierarchy<BasicTalent> talentIHierarchy = hierarchy.transform(function);
        talentIHierarchy.getElements().forEach(talent -> talent.setHierarchy(talentIHierarchy));
        return talentIHierarchy;
    }

}
