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

public record MaybeCrushNearbyEnemiesC2S(double fallingSpeed) implements IPacket {

    public static final CustomPacketPayload.Type<MaybeCrushNearbyEnemiesC2S> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("stuff.maybe_crush_nearby_enemies"));
    public static final StreamCodec<ByteBuf, MaybeCrushNearbyEnemiesC2S> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, MaybeCrushNearbyEnemiesC2S::fallingSpeed,
            MaybeCrushNearbyEnemiesC2S::new);

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer)
            StuffHandlers.maybeCrushNearbyEnemies(serverPlayer, fallingSpeed);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
