package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.world.phys.Vec3;

public class FantazicMath {
    public static boolean withinClamp(int min, int max, int num) {
        return num >= min && num <= max;
    }
    public static boolean withinClamp(float min, float max, float num) {
        return num >= min && num <= max;
    }
    public static boolean withinClamp(double min, double max, double num) {
        return num >= min && num <= max;
    }

    public static Vec3 findCenter(Vec3 point1, Vec3 point2) {
        Vec3 vec3 = point1.subtract(point2);
        Vec3 vec31 = vec3.scale(0.5f);
        return point2.add(vec31);
    }
}
