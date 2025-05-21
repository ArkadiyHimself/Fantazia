package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PerformDoubleJumpC2S(boolean flying) implements IPacket {

    public static final CustomPacketPayload.Type<PerformDoubleJumpC2S> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.perform_double_jump"));
    public static final StreamCodec<ByteBuf, PerformDoubleJumpC2S> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PerformDoubleJumpC2S::flying,
            PerformDoubleJumpC2S::new
    );

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) context.enqueueWork(() -> AttachmentModifyHandlers.performDoubleJump(player, flying));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
