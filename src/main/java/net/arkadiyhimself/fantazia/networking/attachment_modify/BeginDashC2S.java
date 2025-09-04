package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BeginDashC2S() implements IPacket {

    public static final CustomPacketPayload.Type<BeginDashC2S> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.begin_dash"));
    public static final StreamCodec<ByteBuf, BeginDashC2S> CODEC = StreamCodec.unit(new BeginDashC2S());

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) context.enqueueWork(() -> AttachmentModifyHandlers.beginDash(serverPlayer));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
