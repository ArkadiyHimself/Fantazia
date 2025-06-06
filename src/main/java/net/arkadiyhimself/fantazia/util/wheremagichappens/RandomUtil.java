package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.phys.Vec3;

public class RandomUtil {

    public static Vec3 randomHorizontalVec3() {
        return new Vec3(Fantazia.RANDOM.nextDouble(-1,1), 0, Fantazia.RANDOM.nextDouble(-1,1));
    }

    public static Vec3 randomVec3() {
        return new Vec3(Fantazia.RANDOM.nextDouble(-1,1), Fantazia.RANDOM.nextDouble(-1,1), Fantazia.RANDOM.nextDouble(-1,1));
    }

    public static int nextInt(int min, int max) {
        return Fantazia.RANDOM.nextInt(min, max);
    }

    public static float nextFloat(float min, float max) {
        return Fantazia.RANDOM.nextFloat(min, max);
    }

    public static float nextFloat() {
        return Fantazia.RANDOM.nextFloat();
    }

    public static double nextDouble(double min, double max) {
        return Fantazia.RANDOM.nextDouble(min, max);
    }

    public static double nextDouble(double max) {
        return Fantazia.RANDOM.nextDouble(max);
    }

    public static double nextDouble() {
        return Fantazia.RANDOM.nextDouble();
    }
}
