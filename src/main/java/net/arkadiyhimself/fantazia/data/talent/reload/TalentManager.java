package net.arkadiyhimself.fantazia.data.talent.reload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.arkadiyhimself.fantazia.data.talent.TalentType;
import net.arkadiyhimself.fantazia.data.talent.types.BasicTalent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TalentManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    // talent's id || talent itself
    private static final Map<ResourceLocation, BasicTalent> TALENTS = Maps.newHashMap();
    public TalentManager() {
        super(GSON,"talent_reload/talent");
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        TALENTS.clear();
        jsonElementMap.forEach(TalentManager::readTalent);
    }

    private static void readTalent(ResourceLocation id, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String ident = jsonObject.get("type").getAsString();
        BasicTalent talent = GSON.fromJson(jsonElement, TalentType.byId(ident).getBuilderClass()).build();
        TALENTS.put(id, talent);
    }

    public static ImmutableMap<ResourceLocation, BasicTalent> getTalents() {
        return ImmutableMap.copyOf(TALENTS);
    }

    public static void addAttributeTalent(ResourceLocation id, BasicTalent talent) {
        TALENTS.put(id, talent);
    }

    public static @Nullable AdvancementHolder getAdvancement(BasicTalent talent, MinecraftServer server) {
        ResourceLocation advID = talent.getAdvancement();
        if (advID == null) return null;
        return server.getAdvancements().get(advID);
    }

}