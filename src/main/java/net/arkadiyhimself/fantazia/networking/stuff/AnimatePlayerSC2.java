package net.arkadiyhimself.fantazia.networking.stuff;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AnimatePlayerSC2(String anim, int id) implements IPacket {

    public static final CustomPacketPayload.Type<AnimatePlayerSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("stuff.play_animation"));
    public static final StreamCodec<ByteBuf, AnimatePlayerSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AnimatePlayerSC2::anim,
            ByteBufCodecs.INT, AnimatePlayerSC2::id,
            AnimatePlayerSC2::new);

    public AnimatePlayerSC2(String anim, ServerPlayer player) {
        this(anim, player.getId());
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> StuffHandlers.animatePlayer(anim, id));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
