package net.arkadiyhimself.fantazia.data.spawn;

import com.google.gson.*;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EffectsOnSpawnManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();

    private static final List<EffectSpawnHolder.Builder> effectSpawnHolders = Lists.newArrayList();

    public EffectsOnSpawnManager() {
        super(GSON, Fantazia.MODID + "/effect_on_spawn");
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        effectSpawnHolders.clear();
        pObject.values().forEach(EffectsOnSpawnManager::readEffectSpawnInstance);
    }

    private static void readEffectSpawnInstance(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        EffectSpawnHolder.Builder builder = new EffectSpawnHolder.Builder();

        JsonArray entityTypes = object.getAsJsonArray("entity_types");
        for (JsonElement entityType : entityTypes.asList()) builder.addEntityType(ResourceLocation.parse(entityType.getAsString()));

        if (object.has("effect_instances")) for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("effect_instances").entrySet()) {
            JsonObject effectInstance = entry.getValue().getAsJsonObject();

            ResourceLocation effectID = ResourceLocation.parse(entry.getKey());
            Optional<Holder.Reference<MobEffect>> mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(effectID);
            if (mobEffect.isEmpty()) continue;

            double chance = effectInstance.get("chance").getAsDouble();
            int level = effectInstance.get("level").getAsInt();
            boolean hidden = effectInstance.get("hidden").getAsBoolean();

            builder.addEffectInstance(mobEffect.get(), chance, level, hidden);
        }

        if (object.has("aura_instances")) for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("aura_instances").entrySet()) {
            ResourceLocation effectID = ResourceLocation.parse(entry.getKey());
            Optional<Holder.Reference<BasicAura<?>>> basicAuraReference = FantazicRegistries.AURAS.getHolder(effectID);
            if (basicAuraReference.isEmpty()) continue;

            double chance = entry.getValue().getAsDouble();

            builder.addAuraInstance(basicAuraReference.get(), chance);
        }

        effectSpawnHolders.add(builder);
    }

    public static List<EffectSpawnHolder> createHolders() {
        List<EffectSpawnHolder> holders = Lists.newArrayList();
        for (EffectSpawnHolder.Builder builder : effectSpawnHolders) holders.add(builder.build());
        return holders;
    }
}
