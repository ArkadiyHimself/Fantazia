package net.arkadiyhimself.fantazia.data.effect_from_damage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ServerEffectFromDamageManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();
    private static final List<EffectFromDamage> EFFECTS_FROM_DAMAGE = Lists.newArrayList();

    public ServerEffectFromDamageManager() {
        super(GSON, Registries.elementsDirPath(FantazicRegistries.Keys.EFFECT_FROM_DAMAGE));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsonElementMap, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        EFFECTS_FROM_DAMAGE.clear();
        jsonElementMap.forEach(ServerEffectFromDamageManager::readEffects);
    }

    private static void readEffects(ResourceLocation location, JsonElement element) {
        EffectFromDamage.Builder.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(Fantazia.LOGGER::error).ifPresent(builder -> EFFECTS_FROM_DAMAGE.add(builder.build()));
    }

    public static void tryApplyEffects(LivingEntity livingEntity, Holder<DamageType> damageType) {
        for (EffectFromDamage effectFromDamage : EFFECTS_FROM_DAMAGE) effectFromDamage.tryApplyEffects(livingEntity, damageType);
    }
}
