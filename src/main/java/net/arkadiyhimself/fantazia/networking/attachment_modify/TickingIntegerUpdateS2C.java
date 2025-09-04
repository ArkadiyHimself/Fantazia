package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TickingIntegerUpdateS2C(ResourceLocation location, int value, int id) implements IPacket {

    public static final Type<TickingIntegerUpdateS2C> TYPE = new Type<>(Fantazia.location("data_attachment_modify.ticking_integer_update"));

    public static final StreamCodec<ByteBuf, TickingIntegerUpdateS2C> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, TickingIntegerUpdateS2C::location,
            ByteBufCodecs.INT, TickingIntegerUpdateS2C::value,
            ByteBufCodecs.INT, TickingIntegerUpdateS2C::id,
            TickingIntegerUpdateS2C::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.tickingIntegerUpdate(location, value, id));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
