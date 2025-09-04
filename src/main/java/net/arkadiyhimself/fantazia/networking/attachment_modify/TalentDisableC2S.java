package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TalentDisableC2S(ResourceLocation location) implements IPacket {

    public static final CustomPacketPayload.Type<TalentDisableC2S> TYPE = new Type<>(Fantazia.location("data_attachment_modify.talent_disable"));

    public static final StreamCodec<ByteBuf, TalentDisableC2S> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, TalentDisableC2S::location,
            TalentDisableC2S::new);

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) AttachmentModifyHandlers.talendDisable(serverPlayer, location);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
