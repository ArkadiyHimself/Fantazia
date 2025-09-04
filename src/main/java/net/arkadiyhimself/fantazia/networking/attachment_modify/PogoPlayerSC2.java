package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PogoPlayerSC2() implements IPacket {

    public static final CustomPacketPayload.Type<PogoPlayerSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.pogo_player"));
    public static final StreamCodec<ByteBuf, PogoPlayerSC2> CODEC = StreamCodec.unit(new PogoPlayerSC2());

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(AttachmentModifyHandlers::pogoPlayer);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
