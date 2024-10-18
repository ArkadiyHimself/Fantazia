package net.arkadiyhimself.fantazia.data.talent.types;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DoubleJumpHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Items;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ITalentBuilder<T extends ITalent> {
    T build(ResourceLocation identifier);

    ITalentBuilder<T> readExtra(JsonObject jsonObject);

    abstract class AbstractBuilder<T extends ITalent> implements ITalentBuilder<T> {

        private final ResourceLocation iconTexture;
        private final String title;
        private final int wisdom;
        private final ResourceLocation advancement;

        private List<ResourceKey<DamageType>> damageImmunities = Lists.newArrayList();
        private Map<ResourceKey<DamageType>, Float> damageMultipliers = Maps.newHashMap();

        public AbstractBuilder(ResourceLocation iconTexture, String title, int wisdom, ResourceLocation advancement) {
            this.advancement = advancement;
            this.iconTexture = iconTexture;
            this.title = title;
            this.wisdom = wisdom;
        }

        @Override
        public ITalentBuilder<T> readExtra(JsonObject jsonObject) {
            JsonElement immunities = jsonObject.get("damage_immunities");

            List<ResourceKey<DamageType>> list = Lists.newArrayList();

            if (immunities != null) for (JsonElement jsonElement : immunities.getAsJsonArray()) {
                ResourceLocation damageTypeId = ResourceLocation.parse(jsonElement.getAsString());
                list.add(ResourceKey.create(Registries.DAMAGE_TYPE, damageTypeId));
            }

            this.damageImmunities = list;

            JsonElement multipliers = jsonObject.get("damage_multipliers");

            Map<ResourceKey<DamageType>, Float> map = Maps.newHashMap();
            if (multipliers != null) for (Map.Entry<String, JsonElement> entry : multipliers.getAsJsonObject().entrySet()) {
                ResourceLocation damageTypeId = ResourceLocation.parse(entry.getKey());
                map.put(ResourceKey.create(Registries.DAMAGE_TYPE, damageTypeId), entry.getValue().getAsFloat());
            }
            this.damageMultipliers = map;

            return this;
        }

        protected ITalent.BasicProperties buildProperties(ResourceLocation identifier) {
            return new ITalent.BasicProperties(identifier, iconTexture, title, wisdom, advancement, damageImmunities, damageMultipliers);
        }
    }
}
