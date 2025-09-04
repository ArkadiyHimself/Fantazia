package net.arkadiyhimself.fantazia.networking.attachment_modify;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ArrowIsFurious(int id) implements IPacket {

    public static final Type<ArrowIsFurious> TYPE = new Type<>(Fantazia.location("data_attachment_modify.arrow_is_furious"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArrowIsFurious> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ArrowIsFurious::id,
            ArrowIsFurious::new
    );

    @Override
    public void handle(IPayloadContext context) {
        AttachmentModifyHandlers.arrowIsFurious(id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
