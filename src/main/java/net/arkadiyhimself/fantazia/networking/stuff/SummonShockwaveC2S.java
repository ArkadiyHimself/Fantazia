package net.arkadiyhimself.fantazia.networking.stuff;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SummonShockwaveC2S() implements IPacket {

    public static final Type<SummonShockwaveC2S> TYPE = new Type<>(Fantazia.location("stuff.summon_shockwave"));
    public static final StreamCodec<ByteBuf, SummonShockwaveC2S> CODEC = StreamCodec.unit(new SummonShockwaveC2S());

    @Override
    public void handle(IPayloadContext context) {
        StuffHandlers.summonShockwave(context);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
