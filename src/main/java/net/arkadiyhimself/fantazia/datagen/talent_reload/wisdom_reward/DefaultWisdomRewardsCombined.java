package net.arkadiyhimself.fantazia.datagen.talent_reload.wisdom_reward;

import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardCategories;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardsCombined;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.function.Consumer;

public class DefaultWisdomRewardsCombined implements SubProvider<WisdomRewardsCombinedHolder> {

    public static DefaultWisdomRewardsCombined create() {
        return new DefaultWisdomRewardsCombined();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<WisdomRewardsCombinedHolder> consumer) {
        WisdomRewardsCombined.builder(WisdomRewardCategories.BREWED, 8)
                .addReward(Objects.requireNonNull(MobEffects.NIGHT_VISION.getKey()).location(), 15)
                .addReward(Objects.requireNonNull(MobEffects.FIRE_RESISTANCE.getKey()).location(), 20)
                .addReward(Objects.requireNonNull(MobEffects.WATER_BREATHING.getKey()).location(), 15)
                .save(consumer, WisdomRewardCategories.BREWED);

        WisdomRewardsCombined.builder(WisdomRewardCategories.CONSUMED, 4)
                .addReward(BuiltInRegistries.ITEM.getKey(Items.GOLDEN_APPLE), 25)
                .addReward(BuiltInRegistries.ITEM.getKey(Items.ENCHANTED_GOLDEN_APPLE), 45)
                .save(consumer, WisdomRewardCategories.CONSUMED);

        WisdomRewardsCombined.builder(WisdomRewardCategories.CRAFTED, 1)
                .addReward(BuiltInRegistries.ITEM.getKey(Items.BEACON), 30)
                .save(consumer, WisdomRewardCategories.CRAFTED);

        WisdomRewardsCombined.builder(WisdomRewardCategories.SLAYED, 5)
                .addReward(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ENDER_DRAGON), 25)
                .addReward(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITHER),25)
                .addReward(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.RAVAGER),10)
                .addReward(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WARDEN),15)
                .save(consumer, WisdomRewardCategories.SLAYED);

        WisdomRewardsCombined.builder(WisdomRewardCategories.TAMED, 10).save(consumer, WisdomRewardCategories.TAMED);

        WisdomRewardsCombined.builder(WisdomRewardCategories.VISITED, 50).save(consumer, WisdomRewardCategories.VISITED);
    }
}
