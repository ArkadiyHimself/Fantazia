package net.arkadiyhimself.fantazia.data.loot;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class LootInstanceManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .setPrettyPrinting().create();
    private static final List<LootModifierHolder.Builder> LOOT_MODIFIER_HOLDER_MAP = Lists.newArrayList();
    public LootInstanceManager() {
        super(GSON, "loot_instances");
    }
    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        LOOT_MODIFIER_HOLDER_MAP.clear();
        jsonElementMap.forEach(LootInstanceManager::readLoot);
    }
    private static void readLoot(ResourceLocation location, JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        JsonArray array = object.getAsJsonArray("loot_tables");
        LootModifierHolder.Builder builder = new LootModifierHolder.Builder();

        for (JsonElement loot_tables : array.asList()) builder.addLootTable(new ResourceLocation(loot_tables.getAsString()));

        JsonObject instances = object.getAsJsonObject("loot_instances");

        for (Map.Entry<String, JsonElement> entry : instances.entrySet()) {
            JsonObject lootInstance = entry.getValue().getAsJsonObject();

            ResourceLocation itemID = new ResourceLocation(entry.getKey());
            Item item = ForgeRegistries.ITEMS.getValue(itemID);
            if (item == null) continue;

            double chance = lootInstance.get("chance").getAsDouble();

            Item replaced = null;
            if (lootInstance.has("replaced")) {
                ResourceLocation replacedID = new ResourceLocation(lootInstance.get("replaced").getAsString());
                replaced = ForgeRegistries.ITEMS.getValue(replacedID);
            }

            boolean firstTime = lootInstance.has("first_time") && lootInstance.get("first_time").getAsBoolean();
            builder.addLootInstance(item, chance, replaced, firstTime);
        }
        LOOT_MODIFIER_HOLDER_MAP.add(builder);
    }
    public static List<LootModifierHolder> createModifiers() {
        List<LootModifierHolder> holders = Lists.newArrayList();
        for (LootModifierHolder.Builder builder : LOOT_MODIFIER_HOLDER_MAP) holders.add(builder.build());
        return holders;
    }
}
