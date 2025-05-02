package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResetEuphoriaSC2() implements IPacket {

    public static final CustomPacketPayload.Type<ResetEuphoriaSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.reset_euphoria"));

    public static final StreamCodec<ByteBuf, ResetEuphoriaSC2> CODEC = StreamCodec.unit(new ResetEuphoriaSC2());

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(AttachmentModifyHandlers::resetEuphoria);
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
