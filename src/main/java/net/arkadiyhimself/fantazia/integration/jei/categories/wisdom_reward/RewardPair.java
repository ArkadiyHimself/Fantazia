package net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward;

import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardInstance;
import net.minecraft.resources.ResourceLocation;
import oshi.util.tuples.Pair;

public class RewardPair extends Pair<ResourceLocation, WisdomRewardInstance.Builder> {

    private final boolean isDefault;

    public RewardPair(ResourceLocation location, WisdomRewardInstance.Builder builder, boolean isDefault) {
        super(location, builder);
        this.isDefault = isDefault;
    }

    public RewardPair(ResourceLocation location, WisdomRewardInstance.Builder builder) {
        this(location, builder, false);
    }

    public boolean isDefault() {
        return isDefault;
    }
}
