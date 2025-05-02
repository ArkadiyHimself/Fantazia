package net.arkadiyhimself.fantazia.packets.stuff;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AddChasingParticlesS2C(List<ParticleOptions> particleOptions) implements IPacket {

    public static final CustomPacketPayload.Type<AddChasingParticlesS2C> TYPE = new Type<>(Fantazia.res("stuff.chasing_particle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddChasingParticlesS2C> CODEC = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC.apply(ByteBufCodecs.list()), AddChasingParticlesS2C::particleOptions,
            AddChasingParticlesS2C::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> StuffHandlers.chasingParticle(particleOptions));
    }
}
