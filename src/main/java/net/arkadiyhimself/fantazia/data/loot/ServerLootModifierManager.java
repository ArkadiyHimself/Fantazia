package net.arkadiyhimself.fantazia.data.loot;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
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

public class ServerLootModifierManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, LootModifier.Builder> LOOT_MODIFIER_HOLDERS = Maps.newHashMap();

    public ServerLootModifierManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.LOOT_MODIFIER));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        LOOT_MODIFIER_HOLDERS.clear();
        jsonElementMap.forEach(ServerLootModifierManager::readLoot);
    }

    private static void readLoot(ResourceLocation location, JsonElement element) {
        Optional<LootModifier.Builder> optionalBuilder = LootModifier.Builder.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(Fantazia.LOGGER::error);
        optionalBuilder.ifPresent(builder -> LOOT_MODIFIER_HOLDERS.put(location, builder));
    }

    public static Map<ResourceLocation, LootModifier> createModifiers() {
        Map<ResourceLocation, LootModifier> modifierMap = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, LootModifier.Builder> entry : LOOT_MODIFIER_HOLDERS.entrySet())
            modifierMap.put(entry.getKey(), entry.getValue().build());
        return modifierMap;
    }
}
