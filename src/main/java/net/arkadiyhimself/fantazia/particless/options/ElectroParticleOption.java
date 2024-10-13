package net.arkadiyhimself.fantazia.particless.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;


public class ElectroParticleOption extends EntityChasingParticleOption<ParticleType<ElectroParticleOption>> {

    public static MapCodec<ElectroParticleOption> codec(ParticleType<ElectroParticleOption> particleType) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("id").forGetter(ElectroParticleOption::getEntityId),
                Vec3.CODEC.fieldOf("relative").forGetter(ElectroParticleOption::getRelative)
        ).apply(instance, (var1, var2) -> new ElectroParticleOption(var1, var2, particleType))
        );
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, ElectroParticleOption> streamCodec(ParticleType<ElectroParticleOption> particleType) {
        return StreamCodec.composite(
                ByteBufCodecs.INT,
                ElectroParticleOption::getEntityId,
                ByteBufCodecs.VECTOR3F,
                ElectroParticleOption::getVector3f,
                (var1, var2) -> new ElectroParticleOption(var1, var2, particleType)
        );
    }

    public ElectroParticleOption(int id, Vec3 relative, ParticleType<ElectroParticleOption> type) {
        super(id, relative, type);
    }

    public ElectroParticleOption(int id, Vector3f vector3f, ParticleType<ElectroParticleOption> type) {
        super(id, new Vec3(vector3f), type);
    }
}
