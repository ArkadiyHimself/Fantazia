package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CancelDashS2C() implements IPacket {

    public static final Type<CancelDashS2C> TYPE = new Type<>(Fantazia.location("data_attachment_modify.cancel_dash"));
    public static final StreamCodec<ByteBuf, CancelDashS2C> CODEC = StreamCodec.unit(new CancelDashS2C());

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) context.enqueueWork(() -> AttachmentModifyHandlers.cancelDash(serverPlayer));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
