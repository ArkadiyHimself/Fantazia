package net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LocationHolder{

    public static final Codec<LocationHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3.CODEC.fieldOf("location").forGetter(LocationHolder::position),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(LocationHolder::dimension),
            Codec.BOOL.fieldOf("empty").forGetter(LocationHolder::empty)
    ).apply(instance, LocationHolder::decode));

    private static LocationHolder decode(Vec3 position, ResourceKey<Level> dimension, boolean empty) {
        LocationHolder holder = new LocationHolder();
        holder.position = position;
        holder.dimension = dimension;
        holder.empty = empty;
        return holder;
    }

    private Vec3 position;
    private ResourceKey<Level> dimension;
    private boolean empty = true;

    public LocationHolder() {
        this.position = Vec3.ZERO;
        this.dimension = Level.OVERWORLD;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", position.x);
        tag.putDouble("y", position.y);
        tag.putDouble("z", position.z);

        tag.putString("dimension", dimension.location().toString());
        tag.putBoolean("empty", empty);

        return tag;
    }

    public void deserialize(CompoundTag tag) {
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");

        ResourceLocation location = ResourceLocation.parse(tag.getString("dimension"));

        this.position = new Vec3(x, y, z);
        this.dimension = ResourceKey.create(Registries.DIMENSION, location);
        this.empty = tag.getBoolean("empty");
    }

    public Vec3 position() {
        return position;
    }

    public ResourceKey<Level> dimension() {
        return dimension;
    }

    public boolean empty() {
        return empty;
    }

    public GlobalPos globalPos() {
        return new GlobalPos(dimension, BlockPos.containing(position));
    }

    public boolean isIn(Level level) {
        return level.dimension().location().equals(dimension.location());
    }

    public void setLocation(@Nullable Vec3 vec3, @Nullable ResourceKey<Level> key) {
        if (vec3 == null && key == null) return;
        this.position = vec3;
        this.dimension = key;
        this.empty = false;
    }

    public void setLocation(@Nullable Vec3 vec3) {
        if (vec3 == null) return;
        this.position = vec3;
        this.empty = false;
    }
}
