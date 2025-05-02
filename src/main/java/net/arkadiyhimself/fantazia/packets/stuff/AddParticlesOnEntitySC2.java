package net.arkadiyhimself.fantazia.packets.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AddParticlesOnEntitySC2(int id, ParticleOptions options, ParticleMovement movement, int amount, float range) implements IPacket {

    public static final CustomPacketPayload.Type<AddParticlesOnEntitySC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("stuff.add_particles_on_entity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddParticlesOnEntitySC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AddParticlesOnEntitySC2::id,
            ParticleTypes.STREAM_CODEC, AddParticlesOnEntitySC2::options,
            NeoForgeStreamCodecs.enumCodec(ParticleMovement.class), AddParticlesOnEntitySC2::movement,
            ByteBufCodecs.INT, AddParticlesOnEntitySC2::amount,
            ByteBufCodecs.FLOAT, AddParticlesOnEntitySC2::range,
            AddParticlesOnEntitySC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> StuffHandlers.addParticleOnEntity(id, options, movement, amount, range));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
