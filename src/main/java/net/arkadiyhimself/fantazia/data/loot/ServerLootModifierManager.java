package net.arkadiyhimself.fantazia.data.loot;

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

public class ServerLootModifierManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();
    private static final List<LootModifier.Builder> LOOT_MODIFIER_HOLDERS = Lists.newArrayList();

    public ServerLootModifierManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.LOOT_MODIFIER));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        LOOT_MODIFIER_HOLDERS.clear();
        jsonElementMap.forEach(ServerLootModifierManager::readLoot);
    }

    private static void readLoot(ResourceLocation location, JsonElement element) {
        LootModifier.Builder.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(Fantazia.LOGGER::error).ifPresent(LOOT_MODIFIER_HOLDERS::add);
    }

    public static List<LootModifier> createModifiers() {
        List<LootModifier> holders = Lists.newArrayList();
        for (LootModifier.Builder builder : LOOT_MODIFIER_HOLDERS) holders.add(builder.build());
        return holders;
    }
}
