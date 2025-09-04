package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LayeredBarrierDamagedS2C(int id) implements IPacket {

    public static final CustomPacketPayload.Type<LayeredBarrierDamagedS2C> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.layered_barrier_damaged"));

    public static final StreamCodec<ByteBuf, LayeredBarrierDamagedS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LayeredBarrierDamagedS2C::id,
            LayeredBarrierDamagedS2C::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.layeredBarrierDamaged(id));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
