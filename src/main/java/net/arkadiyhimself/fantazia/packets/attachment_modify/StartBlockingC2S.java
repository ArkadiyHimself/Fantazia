package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record StartBlockingC2S() implements IPacket {

    public static final CustomPacketPayload.Type<StartBlockingC2S> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.start_blocking"));
    public static final StreamCodec<ByteBuf, StartBlockingC2S> CODEC = StreamCodec.unit(new StartBlockingC2S());

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) context.enqueueWork(() -> AttachmentModifyHandlers.startBlocking(serverPlayer));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
