package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ReflectActivateSC2(int id) implements IPacket {

    public static final CustomPacketPayload.Type<ReflectActivateSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.reflect_layer_activate"));

    public static final StreamCodec<ByteBuf, ReflectActivateSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ReflectActivateSC2::id,
            ReflectActivateSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.reflectLayerActivate(id));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
