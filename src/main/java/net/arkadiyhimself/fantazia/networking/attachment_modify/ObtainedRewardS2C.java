package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ObtainedRewardS2C(
        ResourceLocation category,
        ResourceLocation instance
) implements IPacket {

    public static final Type<ObtainedRewardS2C> TYPE = new Type<>(Fantazia.location("data_attachment_modify.obtained_reward"));
    public static final StreamCodec<ByteBuf, ObtainedRewardS2C> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ObtainedRewardS2C::category,
            ResourceLocation.STREAM_CODEC, ObtainedRewardS2C::instance,
            ObtainedRewardS2C::new
    );

    @Override
    public void handle(IPayloadContext context) {
        AttachmentModifyHandlers.obtainedReward(category, instance);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
