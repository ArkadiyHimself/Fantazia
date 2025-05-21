package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetDashStoneEntitySC2(int id) implements IPacket {

    public static final CustomPacketPayload.Type<SetDashStoneEntitySC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.set_dashstone_entity"));
    public static final StreamCodec<ByteBuf, SetDashStoneEntitySC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SetDashStoneEntitySC2::id,
            SetDashStoneEntitySC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.setDashStoneEntity(id));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
