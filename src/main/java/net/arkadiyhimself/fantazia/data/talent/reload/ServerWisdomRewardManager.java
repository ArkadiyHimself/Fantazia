package net.arkadiyhimself.fantazia.data.talent.reload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardsCombined;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ServerWisdomRewardManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();

    private static final List<WisdomRewardsCombined.Builder> WISDOM_REWARDS = Lists.newArrayList();

    public ServerWisdomRewardManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.WISDOM_REWARD_CATEGORY));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        WISDOM_REWARDS.clear();
        jsonElementMap.values().forEach(ServerWisdomRewardManager::readRewards);
    }

    private static void readRewards(JsonElement jsonElement) {
        WisdomRewardsCombined.Builder.CODEC.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial(Fantazia.LOGGER::error).ifPresent(WISDOM_REWARDS::add);
    }

    public static List<WisdomRewardsCombined> createWisdomRewards() {
        return ChaoticHierarchy.of(WISDOM_REWARDS).transform(WisdomRewardsCombined.Builder::build).toList();
    }
}
