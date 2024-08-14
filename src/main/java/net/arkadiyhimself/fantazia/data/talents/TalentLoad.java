package net.arkadiyhimself.fantazia.data.talents;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TalentLoad extends SimpleJsonResourceReloadListener {
    private static final Gson TALENT_GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
    private static final Map<ResourceLocation, BasicTalent> TALENTS = new HashMap<>();
    public TalentLoad() {
        super(TALENT_GSON, "talents");
    }
    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        TALENTS.clear();
        map.forEach(this::readTalent);
    }
    private void readTalent(ResourceLocation id, JsonElement jsonElement) {
        BasicTalent talent = TALENT_GSON.fromJson(jsonElement, BasicTalent.Builder.class).build();
        TALENTS.put(id, talent);
    }
    public static ImmutableMap<ResourceLocation, BasicTalent> getTalents() {
        return ImmutableMap.copyOf(TALENTS);
    }
    public static void addTalent(ResourceLocation id, BasicTalent talent) {
        TALENTS.put(id, talent);
    }

}
