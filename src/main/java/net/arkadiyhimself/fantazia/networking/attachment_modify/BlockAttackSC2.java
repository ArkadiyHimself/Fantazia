package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BlockAttackSC2() implements IPacket {

    public static final CustomPacketPayload.Type<BlockAttackSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.block_attack"));
    public static final StreamCodec<ByteBuf, BlockAttackSC2> CODEC = StreamCodec.unit(new BlockAttackSC2());

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(AttachmentModifyHandlers::blockAttack);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
