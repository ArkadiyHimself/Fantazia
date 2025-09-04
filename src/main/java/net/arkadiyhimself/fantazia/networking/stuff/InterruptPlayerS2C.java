package net.arkadiyhimself.fantazia.networking.stuff;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record InterruptPlayerS2C() implements IPacket {

    public static final Type<InterruptPlayerS2C> TYPE = new Type<>(Fantazia.location("stuff.interrupt_player"));

    public static final StreamCodec<ByteBuf, InterruptPlayerS2C> CODEC = StreamCodec.unit(new InterruptPlayerS2C());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(StuffHandlers::interruptPlayer);
    }
}
