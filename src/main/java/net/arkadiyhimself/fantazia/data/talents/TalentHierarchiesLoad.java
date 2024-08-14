package net.arkadiyhimself.fantazia.data.talents;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.arkadiyhimself.fantazia.util.library.hierarchy.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalentHierarchiesLoad extends SimpleJsonResourceReloadListener {
    private static final Gson HIERARCHY_GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .setPrettyPrinting().create();
    private static final Map<String, Map<ResourceLocation, IHierarchy<ResourceLocation>>> TABS = Maps.newHashMap();
    private static final Map<ResourceLocation, IHierarchy<ResourceLocation>> ABILITIES = new HashMap<>();
    private static final Map<ResourceLocation, IHierarchy<ResourceLocation>> STATS_WISDOM = new HashMap<>();
    public TalentHierarchiesLoad() {
        super(HIERARCHY_GSON, "talent_hierarchies");
        TABS.put("abilities", ABILITIES);
        TABS.put("stats_wisdom", STATS_WISDOM);
    }
    public static Map<ResourceLocation, IHierarchy<ResourceLocation>> abilities() {
        return ABILITIES;
    }
    public static Map<ResourceLocation, IHierarchy<ResourceLocation>> statsWisdom() {
        return STATS_WISDOM;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) throws TalentDataException {
        TABS.values().forEach(Map::clear);
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonObject object = entry.getValue().getAsJsonObject();
            boolean flag = object.has("attribute_chain") && object.get("attribute_chain").getAsBoolean();
            if (flag) {
                ChainHierarchy<ResourceLocation> chainHierarchy = attributeChain(entry.getKey(), object.get("chains").getAsInt());
                JsonObject object1 = object.getAsJsonObject("element");
                for (ResourceLocation resourceLocation : chainHierarchy.getElements()) TalentLoad.addTalent(resourceLocation, HIERARCHY_GSON.fromJson(object1, AttributeTalent.Builder.class).build());
                String tab = object.get("tab").getAsString();
                if (TABS.containsKey(tab)) TABS.get(tab).put(entry.getKey(), chainHierarchy);
                continue;
            }
            String hierarchyType = object.get("hierarchy").getAsString();
            HierarchyType type = HierarchyType.typeFromString(hierarchyType);
            MonoHierarchy<ResourceLocation> talentHierarchy = switch (type) {
                case MONO -> createMonoHierarchy(object);
                case CHAIN -> createChainHierarchy(object);
                case CHAOTIC -> createChaoticHierarchy(object);
            };
            String tab = object.get("tab").getAsString();
            if (TABS.containsKey(tab)) TABS.get(tab).put(entry.getKey(), talentHierarchy);
        }
    }
    private MonoHierarchy<ResourceLocation> createMonoHierarchy(JsonObject jsonObject) {
        String id = jsonObject.get("value").getAsString();
        ResourceLocation resourceLocation = new ResourceLocation(id);
        return MonoHierarchy.of(resourceLocation);
    }
    private ChainHierarchy<ResourceLocation> createChainHierarchy(JsonObject jsonObject) {
        List<JsonElement> values = jsonObject.get("values").getAsJsonArray().asList();
        List<ResourceLocation> resourceLocations = Lists.newArrayList();
        for (JsonElement element : values) resourceLocations.add(new ResourceLocation(element.getAsString()));
        return ChainHierarchy.of(resourceLocations);
    }
    private ChaoticHierarchy<ResourceLocation> createChaoticHierarchy(JsonObject jsonObject) {
        List<JsonElement> values = jsonObject.get("values").getAsJsonArray().asList();
        List<ResourceLocation> resourceLocations = Lists.newArrayList();
        for (JsonElement element : values) resourceLocations.add(new ResourceLocation(element.getAsString()));
        return ChaoticHierarchy.of(resourceLocations);
    }
    private ChainHierarchy<ResourceLocation> attributeChain(ResourceLocation basic, int amount) {
        List<ResourceLocation> list = Lists.newArrayList();
        for (int i = 1; i <= amount; i++) list.add(basic.withSuffix(String.valueOf(i)));
        return ChainHierarchy.of(list);
    }
}
