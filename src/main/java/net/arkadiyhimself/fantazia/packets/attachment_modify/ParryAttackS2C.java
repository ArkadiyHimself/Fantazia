package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ParryAttackS2C(float amount) implements IPacket {

    public static final CustomPacketPayload.Type<ParryAttackS2C> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_modify.parry_attack"));
    public static final StreamCodec<ByteBuf, ParryAttackS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ParryAttackS2C::amount,
            ParryAttackS2C::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.parryAttack(amount));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
