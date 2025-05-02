package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ReflectLayerActivateSC2() implements IPacket {

    public static final CustomPacketPayload.Type<ReflectLayerActivateSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.reflect_layer_activate"));

    public static final StreamCodec<ByteBuf, ReflectLayerActivateSC2> CODEC = StreamCodec.unit(new ReflectLayerActivateSC2());

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(AttachmentModifyHandlers::reflectLayerActivate);
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
