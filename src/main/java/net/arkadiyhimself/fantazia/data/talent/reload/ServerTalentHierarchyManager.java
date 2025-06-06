package net.arkadiyhimself.fantazia.data.talent.reload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.talent.TalentDataException;
import net.arkadiyhimself.fantazia.data.talent.TalentHierarchyBuilder;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServerTalentHierarchyManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    // id of a tab || list of ids of talent hierarchies in the tab
    private static final Map<ResourceLocation, List<ResourceLocation>> TABS = Maps.newHashMap();

    // id of a hierarchy || hierarchy itself, contains ids of talents
    private static final Map<ResourceLocation, IHierarchy<ResourceLocation>> ALL_HIERARCHIES = Maps.newHashMap();

    public ServerTalentHierarchyManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.TALENT_HIERARCHY));
    }

    public static ImmutableMap<ResourceLocation, IHierarchy<ResourceLocation>> getAllHierarchies() {
        return ImmutableMap.copyOf(ALL_HIERARCHIES);
    }

    public static ImmutableMap<ResourceLocation, List<ResourceLocation>> getTabs() {
        return ImmutableMap.copyOf(TABS);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) throws TalentDataException {
        TABS.clear();
        ALL_HIERARCHIES.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonElementMap.entrySet()) {
            Optional<TalentHierarchyBuilder> optionalBuilder = TalentHierarchyBuilder.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).resultOrPartial(Fantazia.LOGGER::error);

            ResourceLocation hierarchyId = entry.getKey();
            if (optionalBuilder.isPresent()) {
                TalentHierarchyBuilder builder = optionalBuilder.get();
                ResourceLocation tabId = builder.getTab();
                TABS.putIfAbsent(tabId, Lists.newArrayList());
                TABS.get(tabId).add(hierarchyId);
                ALL_HIERARCHIES.put(hierarchyId, builder.build(hierarchyId));
            } else throw new TalentDataException("Could not build talent hierarchy: " + hierarchyId);
        }
    }
}
