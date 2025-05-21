package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetWisdomSC2(int amount) implements IPacket {

    public static final CustomPacketPayload.Type<SetWisdomSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.set_wisdom"));
    public static final StreamCodec<ByteBuf, SetWisdomSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SetWisdomSC2::amount,
            SetWisdomSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.setWisdom(amount));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
