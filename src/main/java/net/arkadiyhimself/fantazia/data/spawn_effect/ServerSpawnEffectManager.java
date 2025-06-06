package net.arkadiyhimself.fantazia.data.spawn_effect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ServerSpawnEffectManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    private static final List<EffectSpawnApplier.Builder> effectSpawnHolders = Lists.newArrayList();

    public ServerSpawnEffectManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.EFFECT_SPAWN_APPLIER));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        effectSpawnHolders.clear();
        pObject.values().forEach(ServerSpawnEffectManager::readEffectSpawnInstance);
    }

    private static void readEffectSpawnInstance(JsonElement element) {
        EffectSpawnApplier.Builder.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(Fantazia.LOGGER::error).ifPresent(effectSpawnHolders::add);
    }

    public static List<EffectSpawnApplier> createHolders() {
        List<EffectSpawnApplier> holders = Lists.newArrayList();
        for (EffectSpawnApplier.Builder builder : effectSpawnHolders) holders.add(builder.build());
        return holders;
    }
}
