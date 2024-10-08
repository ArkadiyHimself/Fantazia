package net.arkadiyhimself.fantazia.networking.packets.attachment_syncing;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributes;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesGetter;
import net.arkadiyhimself.fantazia.api.type.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LevelAttributesUpdateS2C(CompoundTag tag) implements IPacket {
    public static final CustomPacketPayload.Type<LevelAttributesUpdateS2C> TYPE = new Type<>(Fantazia.res("data_attachment_update.level_attributes"));

    public static final StreamCodec<ByteBuf, LevelAttributesUpdateS2C> CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, LevelAttributesUpdateS2C::tag, LevelAttributesUpdateS2C::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) return;
            clientLevel.getData(FTZAttachmentTypes.LEVEL_ATTRIBUTES).syncDeserialize(tag);
        });
    }
}
