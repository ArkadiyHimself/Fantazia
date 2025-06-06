package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record StopDashS2C() implements IPacket {

    public static final CustomPacketPayload.Type<StopDashS2C> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.stop_dash"));
    public static final StreamCodec<ByteBuf, StopDashS2C> CODEC = StreamCodec.unit(new StopDashS2C());

    @Override
    public void handle(IPayloadContext context) {
       context.enqueueWork(AttachmentModifyHandlers::stopDash);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
