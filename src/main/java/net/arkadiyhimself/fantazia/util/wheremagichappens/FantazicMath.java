package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FantazicMath {

    public static boolean isWithin(int min, int max, int num) {
        return num >= min && num <= max;
    }

    public static boolean isWithin(float min, float max, float num) {
        return num >= min && num <= max;
    }

    public static boolean isWithin(double min, double max, double num) {
        return num >= min && num <= max;
    }

    public static Vec3 findCenter(Vec3 point1, Vec3 point2) {
        Vec3 vec3 = point1.subtract(point2);
        Vec3 vec31 = vec3.scale(0.5f);
        return point2.add(vec31);
    }

    public static double intoSin(int tick, int period) {
        return Math.sin(Math.PI * 2 * tick / period);
    }

    public static double intoCos(int tick, int period) {
        return Math.cos(Math.PI * 2 * tick / period);
    }

    public static double intoSin(float tick, float period) {
        return Math.sin(Math.PI * 2 * tick / period);
    }

    public static double intoCos(float tick, float period) {
        return Math.cos(Math.PI * 2 * tick / period);
    }

    public static int toTicks(int hrs, int min, int sec) {
        return (hrs * 60 * 60 + min * 60 + sec) * 20;
    }

    public static AABB boxFromCenterAndSides(Vec3 center, double dx, double dy, double dz) {
        return new AABB(center.x - dx, center.y - dy, center.z - dz, center.x + dx, center.y + dy, center.z + dz);
    }

    public static AABB squareBoxFromCenterAndSide(Vec3 center, double side) {
        return boxFromCenterAndSides(center, side, side, side);
    }
}
