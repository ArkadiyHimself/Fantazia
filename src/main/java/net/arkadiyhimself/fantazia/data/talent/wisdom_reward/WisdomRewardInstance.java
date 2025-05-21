package net.arkadiyhimself.fantazia.data.talent.wisdom_reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public class WisdomRewardInstance {

    private final int reward;
    private boolean awarded = false;

    public WisdomRewardInstance(int reward, boolean awarded) {
        this.reward = reward;
        this.awarded = awarded;
    }

    public WisdomRewardInstance(int reward) {
        this.reward = reward;
    }

    public int award() {
        if (awarded) return 0;
        awarded = true;
        return reward;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("reward", reward);
        tag.putBoolean("awarded", awarded);
        return tag;
    }

    public static WisdomRewardInstance deserialize(CompoundTag tag) {
        int reward = tag.getInt("reward");
        boolean awarded = tag.getBoolean("awarded");
        return new WisdomRewardInstance(reward, awarded);
    }

    public record Builder(int reward) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("reward").forGetter(Builder::reward)
        ).apply(instance, Builder::new));

        public WisdomRewardInstance build() {
            return new WisdomRewardInstance(reward);
        }
    }
}
