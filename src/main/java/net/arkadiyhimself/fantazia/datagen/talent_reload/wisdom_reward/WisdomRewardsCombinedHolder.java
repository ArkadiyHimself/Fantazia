package net.arkadiyhimself.fantazia.datagen.talent_reload.wisdom_reward;

import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardsCombined;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record WisdomRewardsCombinedHolder(ResourceLocation id, WisdomRewardsCombined.Builder builder) {

    public boolean equals(Object other) {
        if (this == other) return true;
        else {
            if (other instanceof WisdomRewardsCombinedHolder wisdomRewardsCombinedHolder) return this.id.equals(wisdomRewardsCombinedHolder.id);
            return false;
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public @NotNull String toString() {
        return this.id.toString();
    }
}
