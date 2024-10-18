package net.arkadiyhimself.fantazia.packets.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.type.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ChasingParticleS2C(ParticleOptions option) implements IPacket {

    public static final CustomPacketPayload.Type<ChasingParticleS2C> TYPE = new Type<>(Fantazia.res("stuff.chasing_particle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChasingParticleS2C> CODEC = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC, ChasingParticleS2C::option,
            ChasingParticleS2C::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) return;
            Minecraft.getInstance().level.addParticle(option,0,0,0,0,0,0);
        });
    }
}
