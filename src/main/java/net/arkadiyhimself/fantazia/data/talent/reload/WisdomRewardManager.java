package net.arkadiyhimself.fantazia.data.talent.reload;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WisdomRewardManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
    private static final ResourceLocation DEFAULT_ENTRY = Fantazia.res("default");
    private static final Map<String, Map<ResourceLocation, Integer>> REWARD_MAPS = Maps.newHashMap();
    public WisdomRewardManager() {
        super(GSON, Fantazia.MODID + "/talent_reload/wisdom_reward");
    }
    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        REWARD_MAPS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonElementMap.entrySet()) readRewards(entry.getKey().getPath(), entry.getValue().getAsJsonObject());
    }
    private static void readRewards(String name, JsonObject jsonObject) {
        Map<ResourceLocation, Integer> rewardMap = getOrCreate(name);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) rewardMap.put(ResourceLocation.parse(entry.getKey()), entry.getValue().getAsInt());
    }
    private static Map<ResourceLocation, Integer> getOrCreate(String name) {
        REWARD_MAPS.computeIfAbsent(name, location -> Maps.newHashMap());
        return REWARD_MAPS.get(name);
    }
    public static int getReward(String name, ResourceLocation id) {
        if (!REWARD_MAPS.containsKey(name)) return 5;
        Map<ResourceLocation, Integer> rewards = REWARD_MAPS.get(name);
        return rewards.getOrDefault(id, rewards.getOrDefault(DEFAULT_ENTRY, 5));
    }
}
