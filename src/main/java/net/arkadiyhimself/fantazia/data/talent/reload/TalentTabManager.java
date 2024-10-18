package net.arkadiyhimself.fantazia.data.talent.reload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.screen.TalentTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TalentTabManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .setPrettyPrinting().create();
    private static final Map<ResourceLocation, TalentTab> TABS = Maps.newHashMap();
    public TalentTabManager() {
        super(GSON, Fantazia.MODID + "/talent_reload/talent_tab");
    }
    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        TABS.clear();
        jsonElementMap.forEach(TalentTabManager::readTab);
    }
    private static void readTab(ResourceLocation id, JsonElement element) {
        TalentTab builder = GSON.fromJson(element, TalentTab.Builder.class).build();
        TABS.put(id, builder);
    }
    public static Map<ResourceLocation, TalentTab> getTabs() {
        return ImmutableMap.copyOf(TABS);
    }
}
