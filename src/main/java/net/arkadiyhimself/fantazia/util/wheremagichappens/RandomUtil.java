package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class RandomUtil {

    private static final Random RANDOM = new Random();

    public static Vec3 randomHorizontalVec3() {
        return new Vec3(RANDOM.nextDouble(-1,1), 0, RANDOM.nextDouble(-1,1));
    }

    public static Vec3 randomVec3() {
        return new Vec3(RANDOM.nextDouble(-1,1), RANDOM.nextDouble(-1,1), RANDOM.nextDouble(-1,1));
    }

    public static int nextInt(int min, int max) {
        return RANDOM.nextInt(min, max);
    }

    public static float nextFloat(float min, float max) {
        return RANDOM.nextFloat(min, max);
    }

    public static float nextFloat() {
        return RANDOM.nextFloat();
    }

    public static double nextDouble(double min, double max) {
        return RANDOM.nextDouble(min, max);
    }

    public static double nextDouble(double max) {
        return RANDOM.nextDouble(max);
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static long nextLong() {
        return RANDOM.nextLong();
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }
}
