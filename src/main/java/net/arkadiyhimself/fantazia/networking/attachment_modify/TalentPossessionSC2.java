package net.arkadiyhimself.fantazia.networking.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TalentPossessionSC2(ResourceLocation location, boolean unlocked) implements IPacket {

    public static final CustomPacketPayload.Type<TalentPossessionSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("data_attachment_modify.talent_possession"));

    public static final StreamCodec<ByteBuf, TalentPossessionSC2> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, TalentPossessionSC2::location,
            ByteBufCodecs.BOOL, TalentPossessionSC2::unlocked,
            TalentPossessionSC2::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.talentPossession(location, unlocked));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
