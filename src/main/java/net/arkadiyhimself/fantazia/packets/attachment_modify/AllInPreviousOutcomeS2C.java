package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AllInPreviousOutcomeS2C(Integer integer) implements IPacket {

    public static final Type<AllInPreviousOutcomeS2C> TYPE = new Type<>(Fantazia.res("data_attachment_modify.all_in_previous_outcome"));

    public static final StreamCodec<ByteBuf, AllInPreviousOutcomeS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AllInPreviousOutcomeS2C::integer,
            AllInPreviousOutcomeS2C::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.allInPreviousOutcome(integer));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
