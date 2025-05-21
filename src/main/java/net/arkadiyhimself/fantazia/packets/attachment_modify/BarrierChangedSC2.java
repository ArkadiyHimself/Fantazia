package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BarrierChangedSC2(int id, float health) implements IPacket {

    public static final Type<BarrierChangedSC2> TYPE = new Type<>(Fantazia.res("data_attachment_modify.barrier_added"));

    public static final StreamCodec<ByteBuf, BarrierChangedSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BarrierChangedSC2::id,
            ByteBufCodecs.FLOAT, BarrierChangedSC2::health,
            BarrierChangedSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.barrierAdded(id, health));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
