package net.arkadiyhimself.fantazia.util.library.pseudorandom;

import net.arkadiyhimself.fantazia.Fantazia;
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
public class PSERANInstance {
    private final double CHANCE;
    private final double initialChance;
    private int fails = 0;
    public PSERANInstance(double CHANCE) {
        this.CHANCE = CHANCE;
        if (CHANCE >= 1) this.initialChance = 1;
        else if (CHANCE <= 0) this.initialChance = 0;
        else this.initialChance = PSERANHelper.calculateC(CHANCE);
    }
    public double getSupposedChance() {
        return CHANCE;
    }
    public boolean performAttempt() {
        if (Fantazia.RANDOM.nextFloat() < getActualChance()) {
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
    public PSERANInstance transform(double newChance) {
        PSERANInstance newInstance = new PSERANInstance(newChance);
        newInstance.fails = this.fails;
        return newInstance;
    }
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("chance", CHANCE);
        tag.putInt("fails", fails);
        return tag;
    }
    public static PSERANInstance deserialize(CompoundTag tag) {
        PSERANInstance instance = new PSERANInstance(tag.getDouble("chance"));
        instance.fails = tag.getInt("fails");
        return instance;
    }
}
