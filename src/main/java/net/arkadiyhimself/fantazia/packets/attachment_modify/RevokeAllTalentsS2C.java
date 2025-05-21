package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RevokeAllTalentsS2C() implements IPacket {

    public static final CustomPacketPayload.Type<RevokeAllTalentsS2C> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.revoke_all_talents"));

    public static final StreamCodec<ByteBuf, RevokeAllTalentsS2C> CODEC = StreamCodec.unit(new RevokeAllTalentsS2C());

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(AttachmentModifyHandlers::revokeAllTalents);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
