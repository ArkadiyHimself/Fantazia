package net.arkadiyhimself.fantazia.util.library;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SPHEREBOX {
    private final double RADIUS;
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    public SPHEREBOX(double radius, double x, double y, double z) {
        this.RADIUS = java.lang.Math.abs(radius);
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
    }
    public SPHEREBOX(float radius, float centerX, float centerY, float centerZ) {
        this((double) radius, (double) centerX, (double) centerY, (double) centerZ);
    }
    public SPHEREBOX(double radius, Vec3 center) {
        this(radius, center.x(), center.y(), center.z());
    }
    public double radius() {
        return RADIUS;
    }
    public double minX() {
        return centerX - RADIUS;
    }
    public double minY() {
        return centerY - RADIUS;
    }
    public double minZ() {
        return centerZ - RADIUS;
    }
    public double maxX() {
        return centerX + RADIUS;
    }
    public double maxY() {
        return centerY + RADIUS;
    }
    public double maxZ() {
        return centerZ + RADIUS;
    }
    public Vec3 getCenter() {
        return new Vec3(centerX, centerY, centerZ);
    }
    public boolean contains(Vec3 position) {
        return actualDistance(position) == 0;
    }
    public boolean contains(BlockPos blockPos) {
        return contains(blockPos.getCenter());
    }
    public boolean contains(Entity entity) {
        return contains(entity.position());
    }
    public Vec3 getRandomPlace(boolean surface) {
        Vec3 place = new Vec3(Fantazia.RANDOM.nextDouble(-1,1), Fantazia.RANDOM.nextDouble(-1,1), Fantazia.RANDOM.nextDouble(-1,1)).normalize();
        double range = surface ? RADIUS : Fantazia.RANDOM.nextDouble(RADIUS);
        return place.scale(range).add(getCenter());
    }
    public double area() {
        return 4f * java.lang.Math.PI * RADIUS * RADIUS;
    }
    public double volume() {
        return 4f / 3 * java.lang.Math.PI * RADIUS * RADIUS * RADIUS;
    }
    public SPHEREBOX inflate(float multiplier) {
        return new SPHEREBOX(RADIUS * multiplier, getCenter());
    }
    public List<Entity> entitiesInside(Level level, Predicate<Entity> filter) {
        if (level == null) return Lists.newArrayList();
        AABB aabb = new AABB(centerX - RADIUS, centerY - RADIUS, centerZ - RADIUS, centerX + RADIUS, centerY + RADIUS, centerZ + RADIUS);
        List<Entity> list = level.getEntitiesOfClass(Entity.class, aabb, filter);
        list.removeIf(Predicate.not(this::contains));
        return list;
    }
    public List<Entity> entitiesInside(Level level, EntityType<? extends Entity> type) {
        return entitiesInside(level, entity -> entity.getType() == type);
    }
    public <T extends Entity> List<T> entitiesInside(Level level, Class<T> cls) {
        List<Entity> entities = entitiesInside(level, cls::isInstance);
        List<T> tList = Lists.newArrayList();
        for (Entity entity : entities) tList.add(cls.cast(entity));
        return tList;
    }
    public List<Entity> entitiesInside(Level level) {
        if (level == null) return new ArrayList<>();
        return entitiesInside(level, entity -> true);
    }
    public List<BlockPos> allBlocksInside() {
        List<BlockPos> blockPoses = new ArrayList<>();
        for (int i = (int) -RADIUS; i <= RADIUS; i++) {
            for (int j = (int) -RADIUS; j <= RADIUS; j++) {
                for (int k = (int) -RADIUS; k <= RADIUS; k++) {
                    BlockPos pos = new BlockPos((int) (centerX + i),(int) centerY + j, (int) centerZ + k);
                    blockPoses.add(pos);
                }
            }
        }
        blockPoses.removeIf(Predicate.not(this::contains));
        return blockPoses;
    }
    public List<BlockPos> blocksInside(Level level, Predicate<BlockPos> filter) {
        if (level == null) return Lists.newArrayList();
        List<BlockPos> blockPoses = new ArrayList<>();
        for (int i = (int) -RADIUS; i <= RADIUS; i++) {
            for (int j = (int) -RADIUS; j <= RADIUS; j++) {
                for (int k = (int) -RADIUS; k <= RADIUS; k++) {
                    BlockPos pos = new BlockPos((int) (centerX + i),(int) centerY + j, (int) centerZ + k);
                    blockPoses.add(pos);
                }
            }
        }
        blockPoses.removeIf(blockPos -> level.getBlockState(blockPos).isAir());
        blockPoses.removeIf(Predicate.not(this::contains));
        blockPoses.removeIf(Predicate.not(filter));
        return blockPoses;
    }
    public List<BlockPos> blocksInside(Level level, Block block) {
        return blocksInside(level, blockPos -> level.getBlockState(blockPos).is(block));
    }
    public List<BlockPos> blocksInside(Level level, Class<? extends Block> cls) {
        return blocksInside(level, blockPos -> level.getBlockState(blockPos).getBlock().getClass() == cls);
    }
    public List<BlockPos> blocksInside(Level level) {
        return blocksInside(level, blockPos -> true);
    }
    public boolean intersects(SPHEREBOX spherebox) {
        double dist = spherebox.getCenter().distanceTo(this.getCenter());
        return dist <= java.lang.Math.abs(spherebox.radius() + this.radius());
    }
    public double actualDistance(Vec3 position) {
        double dist = position.distanceTo(getCenter());
        return dist < RADIUS ? 0 : dist - RADIUS;
    }
    public double actualDistance(double x, double y, double z) {
        return actualDistance(new Vec3(x, y, z));
    }
    public double actualDistance(Entity entity) {
        return actualDistance(entity.position());
    }
    public double actualDistance(BlockPos blockPos) {
        return actualDistance(blockPos.getCenter());
    }
    public static SPHEREBOX fromOppositePoints(Vec3 point1, Vec3 point2) {
        double radius = point1.distanceTo(point2) / 2;
        Vec3 center = FantazicMath.findCenter(point1, point2);
        return new SPHEREBOX(radius, center);
    }
    public static SPHEREBOX fromCenterAndPoint(Vec3 center, Vec3 vec3) {
        double radius = center.distanceTo(vec3);
        return new SPHEREBOX(radius, center);
    }

}
