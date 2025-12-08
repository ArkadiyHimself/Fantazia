package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PassiveBarrierActivationSC2(int id) implements IPacket {

    public static final Type<PassiveBarrierActivationSC2> TYPE = new Type<>(Fantazia.location("data_attachment_modify.passive_barrier_activation"));

    public static final StreamCodec<ByteBuf, PassiveBarrierActivationSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PassiveBarrierActivationSC2::id,
            PassiveBarrierActivationSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        AttachmentModifyHandlers.passiveBarrierActivation(id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
