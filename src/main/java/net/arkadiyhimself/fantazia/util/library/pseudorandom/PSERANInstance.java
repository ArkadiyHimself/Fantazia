package net.arkadiyhimself.fantazia.util.library.pseudorandom;

import net.arkadiyhimself.fantazia.Fantazia;

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
        this.initialChance = CHANCE == 0 ? 0 : PSERANHelper.calculateC(CHANCE);
    }
    public double getSupposedChance() {
        return CHANCE;
    }
    public boolean performAttempt() {
        if (Fantazia.RANDOM.nextFloat() < getActualChance()) {
            // success
            succeed();
            return true;
        } else {
            // fail
            failed();
            return false;
        }
    }
    public double getActualChance() {
        return initialChance * (fails + 1);
    }
    public void succeed() {
        fails = 0;
    }
    public void failed() {
        fails++;
    }
    public PSERANInstance transform(double newChance) {
        PSERANInstance newInstance = new PSERANInstance(newChance);
        newInstance.fails = this.fails;
        return newInstance;
    }
}
