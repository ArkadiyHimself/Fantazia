package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BarrierDamagedS2C(int id, float damage) implements IPacket {

    public static final Type<BarrierDamagedS2C> TYPE = new Type<>(Fantazia.res("data_attachment_modify.barrier_damaged"));

    public static final StreamCodec<ByteBuf, BarrierDamagedS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BarrierDamagedS2C::id,
            ByteBufCodecs.FLOAT, BarrierDamagedS2C::damage,
            BarrierDamagedS2C::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.barrierDamaged(id, damage));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
