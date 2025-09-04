package net.arkadiyhimself.fantazia.util.library.concept_of_consistency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.nbt.CompoundTag;

/**
 * The pseudo-random distribution refers to a statistical mechanic of
 * how certain probability-based items and abilities work.
 * Under a conditional distribution, the event's chance increases
 * every time the event does not occur, but is lower in the first place
 * as compensation. This results in the effects occurring with a lower
 * variance, meaning the proc chance occurs in a narrow-band, and
 * operating under a concave distribution, meaning the proc chance
 * has the highest point.
 */

public class ConCosInstance {

    public static final Codec<ConCosInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("chance").forGetter(cosCon -> cosCon.chance),
            Codec.INT.fieldOf("fails").forGetter(cosCon -> cosCon.fails)
    ).apply(instance, ConCosInstance::decode));

    private static ConCosInstance decode(double chance, int fails) {
        ConCosInstance instance = new ConCosInstance(chance);
        instance.fails = fails;
        return instance;
    }

    private final double chance;
    private final double initialChance;
    private int fails = 0;

    public ConCosInstance(double chance) {
        this.chance = chance;
        if (chance >= 1) this.initialChance = 1;
        else if (chance <= 0) this.initialChance = 0;
        else this.initialChance = ConCosHelper.calculateC(chance);
    }

    public double getSupposedChance() {
        return chance;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean performAttempt() {
        if (RandomUtil.nextFloat() < getActualChance()) {
            // success
            fails = 0;
            return true;
        } else {
            // fail
            fails++;
            return false;
        }
    }

    public double getActualChance() {
        return initialChance * (fails + 1);
    }

    public ConCosInstance transform(double newChance) {
        ConCosInstance newInstance = new ConCosInstance(newChance);
        newInstance.fails = this.fails;
        return newInstance;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("chance", chance);
        tag.putInt("fails", fails);
        return tag;
    }

    public static ConCosInstance deserialize(CompoundTag tag) {
        ConCosInstance instance = new ConCosInstance(tag.getDouble("chance"));
        instance.fails = tag.getInt("fails");
        return instance;
    }
}
