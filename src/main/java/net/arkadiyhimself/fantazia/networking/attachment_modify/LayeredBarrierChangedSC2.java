package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LayeredBarrierChangedSC2(int id, int layers) implements IPacket {

    public static final CustomPacketPayload.Type<LayeredBarrierChangedSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.layered_barrier_added"));

    public static final StreamCodec<ByteBuf, LayeredBarrierChangedSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LayeredBarrierChangedSC2::id,
            ByteBufCodecs.INT, LayeredBarrierChangedSC2::layers,
            LayeredBarrierChangedSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.layeredBarrierAdded(id, layers));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
