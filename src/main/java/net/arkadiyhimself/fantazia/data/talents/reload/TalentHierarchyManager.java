package net.arkadiyhimself.fantazia.data.talents.reload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.arkadiyhimself.fantazia.data.talents.AttributeTalent;
import net.arkadiyhimself.fantazia.data.talents.TalentDataException;
import net.arkadiyhimself.fantazia.util.library.hierarchy.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TalentHierarchyManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .setPrettyPrinting().create();

    // id of a tab || list of ids of talent hierarchies in the tab
    private static final Map<ResourceLocation, List<ResourceLocation>> TABS = Maps.newHashMap();

    // id of a hierarchy || hierarchy itself, contains ids of talents
    private static final Map<ResourceLocation, IHierarchy<ResourceLocation>> ALL_HIERARCHIES = Maps.newHashMap();
    public TalentHierarchyManager() {
        super(GSON, "talent_hierarchies");
    }
    public static ImmutableMap<ResourceLocation, IHierarchy<ResourceLocation>> getAllHierarchies() {
        return ImmutableMap.copyOf(ALL_HIERARCHIES);
    }
    public static ImmutableMap<ResourceLocation, List<ResourceLocation>> getTabs() {
        return ImmutableMap.copyOf(TABS);
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) throws TalentDataException {
        TABS.values().forEach(List::clear);
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonElementMap.entrySet()) {

            ResourceLocation hierarchyID = entry.getKey();
            JsonObject object = entry.getValue().getAsJsonObject();
            if (object.has("attribute_chain") && object.get("attribute_chain").getAsBoolean()) {
                ChainHierarchy<ResourceLocation> chainHierarchy = attributeChain(hierarchyID, object.get("chains").getAsInt());
                JsonObject element = object.getAsJsonObject("element");
                for (ResourceLocation resourceLocation : chainHierarchy.getElements()) TalentManager.addAttributeTalent(resourceLocation, GSON.fromJson(element, AttributeTalent.Builder.class).build());
                ResourceLocation tab = ResourceLocation.parse(object.get("tab").getAsString());
                getOrCreateTabHierarchies(tab).add(hierarchyID);
                ALL_HIERARCHIES.put(hierarchyID, chainHierarchy);
                continue;
            }

            String hierarchyType = object.get("hierarchy").getAsString();
            HierarchyType type = HierarchyType.typeFromString(hierarchyType);
            IHierarchy<ResourceLocation> talentHierarchy = switch (type) {
                case MONO -> createMonoHierarchy(object);
                case CHAIN -> createChainHierarchy(object);
                case CHAOTIC -> createChaoticHierarchy(object);
                case COMPLEX -> null;
            };

            ResourceLocation tab = ResourceLocation.parse(object.get("tab").getAsString());
            getOrCreateTabHierarchies(tab).add(hierarchyID);

            ALL_HIERARCHIES.put(hierarchyID, talentHierarchy);
        }
    }
    private List<ResourceLocation> getOrCreateTabHierarchies(ResourceLocation location) {
        TABS.computeIfAbsent(location, location1 -> Lists.newArrayList());
        return TABS.get(location);
    }
    private MonoHierarchy<ResourceLocation> createMonoHierarchy(JsonObject jsonObject) {
        String id = jsonObject.get("value").getAsString();
        ResourceLocation resourceLocation = ResourceLocation.parse(id);
        return MonoHierarchy.of(resourceLocation);
    }
    private ChainHierarchy<ResourceLocation> createChainHierarchy(JsonObject jsonObject) {
        List<JsonElement> values = jsonObject.get("values").getAsJsonArray().asList();
        List<ResourceLocation> resourceLocations = Lists.newArrayList();
        for (JsonElement element : values) resourceLocations.add(ResourceLocation.parse(element.getAsString()));
        return ChainHierarchy.of(resourceLocations);
    }
    private ChaoticHierarchy<ResourceLocation> createChaoticHierarchy(JsonObject jsonObject) {
        List<JsonElement> values = jsonObject.get("values").getAsJsonArray().asList();
        List<ResourceLocation> resourceLocations = Lists.newArrayList();
        for (JsonElement element : values) resourceLocations.add(ResourceLocation.parse(element.getAsString()));
        return ChaoticHierarchy.of(resourceLocations);
    }
    private ChainHierarchy<ResourceLocation> attributeChain(ResourceLocation basic, int amount) {
        List<ResourceLocation> list = Lists.newArrayList();
        for (int i = 1; i <= amount; i++) list.add(basic.withSuffix(String.valueOf(i)));
        return ChainHierarchy.of(list);
    }
}
