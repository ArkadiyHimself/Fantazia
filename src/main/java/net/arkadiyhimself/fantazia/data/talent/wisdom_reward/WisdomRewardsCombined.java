package net.arkadiyhimself.fantazia.data.talent.wisdom_reward;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.datagen.talent_reload.wisdom_reward.WisdomRewardsCombinedHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public record WisdomRewardsCombined(ResourceLocation category, Map<ResourceLocation, WisdomRewardInstance> rewardMap, int defaultReward) {

    public WisdomRewardInstance getReward(ResourceLocation id) {
        if (!rewardMap.containsKey(id)) rewardMap.put(id, new WisdomRewardInstance(defaultReward));
        return rewardMap.get(id);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();

        for (Map.Entry<ResourceLocation, WisdomRewardInstance> entry : rewardMap().entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", entry.getKey().toString());
            entryTag.put("instance", entry.getValue().serialize());
            listTag.add(entryTag);
        }
        tag.put("instances", listTag);

        tag.putInt("defaultReward", defaultReward);
        tag.putString("category", category().toString());
        return tag;
    }

    public static WisdomRewardsCombined deserialize(CompoundTag tag) {
        Map<ResourceLocation, WisdomRewardInstance> wisdomRewardMap = Maps.newHashMap();

        ListTag listTag = tag.getList("instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag entryTag = listTag.getCompound(i);
            ResourceLocation id = ResourceLocation.parse(entryTag.getString("id"));
            WisdomRewardInstance reward = WisdomRewardInstance.deserialize(entryTag.getCompound("instance"));
            wisdomRewardMap.put(id, reward);
        }

        int defaultReward = tag.getInt("defaultReward");
        ResourceLocation category = ResourceLocation.parse(tag.getString("category"));

        return new WisdomRewardsCombined(category, wisdomRewardMap, defaultReward);
    }

    public static Builder builder(ResourceLocation category, int defaultReward) {
        return new Builder(category, Maps.newHashMap(), defaultReward);
    }

    public record Builder(ResourceLocation category, Map<ResourceLocation, WisdomRewardInstance.Builder> builders, int defaultReward) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("category").forGetter(Builder::category),
                Codec.unboundedMap(ResourceLocation.CODEC, WisdomRewardInstance.Builder.CODEC).fieldOf("rewards").forGetter(Builder::builders),
                Codec.INT.optionalFieldOf("default_reward", 5).forGetter(Builder::defaultReward)
        ).apply(instance, Builder::new));

        public Builder addReward(ResourceLocation location, int reward) {
            builders.put(location, new WisdomRewardInstance.Builder(reward));
            return this;
        }

        public WisdomRewardsCombined build() {
            Map<ResourceLocation, WisdomRewardInstance> map = Maps.newHashMap();
            for (Map.Entry<ResourceLocation, WisdomRewardInstance.Builder> entry : builders.entrySet()) map.put(entry.getKey(), entry.getValue().build());
            return new WisdomRewardsCombined(category, map, defaultReward);
        }

        public WisdomRewardsCombinedHolder holder(ResourceLocation id) {
            return new WisdomRewardsCombinedHolder(id, this);
        }

        public void save(Consumer<WisdomRewardsCombinedHolder> consumer, ResourceLocation id) {
            consumer.accept(holder(id));
        }
    }
}
