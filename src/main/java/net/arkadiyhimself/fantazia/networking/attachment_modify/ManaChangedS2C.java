package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ManaChangedS2C(float value) implements IPacket {

    public static final CustomPacketPayload.Type<ManaChangedS2C> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.mana_changed"));
    public static final StreamCodec<ByteBuf, ManaChangedS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ManaChangedS2C::value,
            ManaChangedS2C::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.manaChanged(value));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
