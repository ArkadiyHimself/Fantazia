package net.arkadiyhimself.fantazia.particless.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
                ).apply(instance, (var1, var2) -> new EntityChasingParticleOption<>(var1, var2, particleType))
        );
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, EntityChasingParticleOption<?>> basicStreamCodec(ParticleType<EntityChasingParticleOption<?>> particleType) {
        return StreamCodec.composite(
                ByteBufCodecs.INT,
                EntityChasingParticleOption::getEntityId,
                ByteBufCodecs.VECTOR3F,
                EntityChasingParticleOption::getVector3f,
                (var1, var2) -> new EntityChasingParticleOption<>(var1, new Vec3(var2), particleType)
        );
    }

    private final int entity;
    private final Vec3 relative;
    private final T particleType;

    public EntityChasingParticleOption(int entity, Vec3 relative, T particleType) {
        this.entity = entity;
        this.relative = relative;
        this.particleType = particleType;
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
}
