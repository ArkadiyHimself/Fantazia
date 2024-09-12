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
    private static final List<LootModifierHolder.Builder> lootModifierHolders = Lists.newArrayList();
    public LootInstanceManager() {
        super(GSON, "loot_instances");
    }
    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        lootModifierHolders.clear();
        jsonElementMap.values().forEach(LootInstanceManager::readLoot);
    }
    private static void readLoot(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        LootModifierHolder.Builder builder = new LootModifierHolder.Builder();

        JsonArray lootTables = object.getAsJsonArray("loot_tables");
        for (JsonElement lootTable : lootTables.asList()) builder.addLootTable(new ResourceLocation(lootTable.getAsString()));

        for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("loot_instances").entrySet()) {
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
        lootModifierHolders.add(builder);
    }
    public static List<LootModifierHolder> createModifiers() {
        List<LootModifierHolder> holders = Lists.newArrayList();
        for (LootModifierHolder.Builder builder : lootModifierHolders) holders.add(builder.build());
        return holders;
    }
}
