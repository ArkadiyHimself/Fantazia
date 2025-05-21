package net.arkadiyhimself.fantazia.data.talent.reload;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.talent.TalentDataException;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class ServerTalentManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    // talent's id || talent itself
    private static final BiMap<ResourceLocation, Talent> TALENTS = HashBiMap.create();

    public ServerTalentManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.TALENT));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        TALENTS.clear();
        jsonElementMap.forEach(ServerTalentManager::readTalent);
    }

    private static void readTalent(ResourceLocation id, JsonElement jsonElement) throws TalentDataException {
        Optional<Talent.Builder> talent = Talent.Builder.CODEC.parse(JsonOps.INSTANCE, jsonElement).resultOrPartial(Fantazia.LOGGER::error);
        if (talent.isPresent()) TALENTS.put(id, talent.get().build(id));
        else throw new TalentDataException("Could not read talent: " + id);
    }

    public static void addSimpleChain(ResourceLocation id, Talent talent) throws TalentDataException {
        if (TALENTS.containsKey(id)) throw new TalentDataException("Already contains the talent: " + id);
        TALENTS.put(id, talent);
    }

    public static @Nullable AdvancementHolder getAdvancement(Talent talent, MinecraftServer server) {
        Optional<ResourceLocation> advID = talent.advancement();
        return advID.map(location -> server.getAdvancements().get(location)).orElse(null);
    }

    public static ImmutableMap<ResourceLocation, Talent> getAllTalents() {
        return TALENTS.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(TALENTS);
    }

    public static @Nullable Talent getTalent(ResourceLocation id) {
        return TALENTS.get(id);
    }

    public static @NotNull ResourceLocation getTalentId(Talent talent) throws TalentDataException {
        return TALENTS.inverse().get(talent);
    }

    public static int totalTalents() {
        return TALENTS.size();
    }
}
