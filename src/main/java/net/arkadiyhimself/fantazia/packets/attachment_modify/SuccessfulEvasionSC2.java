package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SuccessfulEvasionSC2(int id) implements IPacket {

    public static final CustomPacketPayload.Type<SuccessfulEvasionSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.successful_evasion"));

    public static final StreamCodec<ByteBuf, SuccessfulEvasionSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SuccessfulEvasionSC2::id,
            SuccessfulEvasionSC2::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.successfulEvasion(id));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
