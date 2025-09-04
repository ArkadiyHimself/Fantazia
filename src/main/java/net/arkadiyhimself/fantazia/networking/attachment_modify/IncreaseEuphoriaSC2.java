package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record IncreaseEuphoriaSC2() implements IPacket {

    public static final CustomPacketPayload.Type<IncreaseEuphoriaSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.increase_euphoria"));
    public static final StreamCodec<ByteBuf, IncreaseEuphoriaSC2> CODEC = StreamCodec.unit(new IncreaseEuphoriaSC2());

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(AttachmentModifyHandlers::increaseEuphoria);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
