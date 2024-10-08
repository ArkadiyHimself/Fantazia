package net.arkadiyhimself.fantazia.networking.packets.stuff;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.type.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KickOutOfGuiS2C() implements IPacket {

    public static final CustomPacketPayload.Type<KickOutOfGuiS2C> TYPE = new Type<>(Fantazia.res("stuff.kick_out_of_gui"));

    public static final StreamCodec<ByteBuf, KickOutOfGuiS2C> CODEC = StreamCodec.unit(new KickOutOfGuiS2C());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (Minecraft.getInstance().player == null) return;
        context.enqueueWork(() -> {
            Minecraft.getInstance().player.closeContainer();
            Minecraft.getInstance().player.stopUsingItem();
        });
    }
}
