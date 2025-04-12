package net.arkadiyhimself.fantazia.packets.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record AddParticleS2C(Vector3f position, Vector3f delta, ParticleOptions particleOptions) implements IPacket {

    public static final CustomPacketPayload.Type<AddParticleS2C> TYPE = new Type<>(Fantazia.res("stuff.add_particle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddParticleS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, AddParticleS2C::position,
            ByteBufCodecs.VECTOR3F, AddParticleS2C::delta,
            ParticleTypes.STREAM_CODEC, AddParticleS2C::particleOptions,
            AddParticleS2C::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) return;
            Minecraft.getInstance().level.addParticle(particleOptions, position.x(), position.y(), position.z(), delta.x(), delta.y(), delta.z());
        });
    }
}
