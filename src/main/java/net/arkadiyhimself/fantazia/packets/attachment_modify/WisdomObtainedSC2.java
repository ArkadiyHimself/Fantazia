package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WisdomObtainedSC2(int amount) implements IPacket {

    public static final CustomPacketPayload.Type<WisdomObtainedSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.wisdom_obtained"));

    public static final StreamCodec<ByteBuf, WisdomObtainedSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, WisdomObtainedSC2::amount,
            WisdomObtainedSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.wisdomObtained(amount));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
