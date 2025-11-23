package net.arkadiyhimself.fantazia.data.talent.reload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.arkadiyhimself.fantazia.client.screen.TalentTab;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.talent.TalentDataException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ServerTalentTabManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    private static final Map<ResourceLocation, TalentTab> TABS = Maps.newHashMap();

    public ServerTalentTabManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.TALENT_TAB));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        TABS.clear();
        jsonElementMap.forEach(ServerTalentTabManager::readTab);
    }

    private static void readTab(ResourceLocation id, JsonElement element) {
        TalentTab.Builder optional = TalentTab.Builder.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(s -> new TalentDataException("Could not build talent tab: " + id));
        TABS.put(id, optional.build());
    }

    public static Map<ResourceLocation, TalentTab> getTabs() {
        return ImmutableMap.copyOf(TABS);
    }
}
