package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record StaminaChangedSC2(float value, int delay)implements IPacket {

    public static final CustomPacketPayload.Type<StaminaChangedSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.stamina_changed"));
    public static final StreamCodec<ByteBuf, StaminaChangedSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, StaminaChangedSC2::value,
            ByteBufCodecs.INT, StaminaChangedSC2::delay,
            StaminaChangedSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.staminaChanged(value, delay));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
