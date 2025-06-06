package net.arkadiyhimself.fantazia.particless.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class EntityChasingParticleOption<T extends ParticleType<?>> implements ParticleOptions {

    public static MapCodec<EntityChasingParticleOption<?>> basicCodec(ParticleType<EntityChasingParticleOption<?>> particleType) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.INT.fieldOf("id").forGetter(EntityChasingParticleOption::getEntityId),
                        Vec3.CODEC.fieldOf("relative").forGetter(EntityChasingParticleOption::getRelative)
                ).apply(instance, (var1, var2) -> new EntityChasingParticleOption<>(var1, particleType, var2))
        );
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, EntityChasingParticleOption<?>> basicStreamCodec(ParticleType<EntityChasingParticleOption<?>> particleType) {
        return StreamCodec.composite(
                ByteBufCodecs.INT, EntityChasingParticleOption::getEntityId,
                ByteBufCodecs.VECTOR3F, EntityChasingParticleOption::getVector3f,
                (var1, var2) -> new EntityChasingParticleOption<>(var1, particleType, new Vec3(var2))
        );
    }

    private final int entity;
    private final T particleType;
    private final Vec3 relative;

    public EntityChasingParticleOption(int entity, T particleType, float wdt, float hgt) {
        this.entity = entity;
        this.relative = bakeRelative(wdt, hgt);
        this.particleType = particleType;
    }

    private EntityChasingParticleOption(int entity, T particleType, Vec3 relative) {
        this.entity = entity;
        this.particleType = particleType;
        this.relative = relative;
    }

    @Override
    public @NotNull T getType() {
        return particleType;
    }

    public int getEntityId() {
        return entity;
    }

    public Vec3 getRelative() {
        return relative;
    }

    protected Vector3f getVector3f() {
        return getRelative().toVector3f();
    }

    private Vec3 bakeRelative(float wdt, float hgt) {
        Vec3 vec3 = RandomUtil.randomHorizontalVec3().normalize().scale(wdt);
        double x = vec3.x();
        double z = vec3.z();
        double y = RandomUtil.nextDouble(hgt * 0.1, hgt * 0.8);
        return new Vec3(x, y, z);
    }
}
