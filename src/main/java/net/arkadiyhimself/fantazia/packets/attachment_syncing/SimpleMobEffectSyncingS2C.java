package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SimpleMobEffectSyncingS2C(CompoundTag tag, int id) implements IAttachmentSync {

    public static final CustomPacketPayload.Type<SimpleMobEffectSyncingS2C> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_update.simple_mob_effect_syncing"));

    public static final StreamCodec<ByteBuf, SimpleMobEffectSyncingS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SimpleMobEffectSyncingS2C::tag,
            ByteBufCodecs.INT, SimpleMobEffectSyncingS2C::id,
            SimpleMobEffectSyncingS2C::new);

    @Override
    public void handle(IPayloadContext context) {
        simpleMobEffectSyncing(tag, id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
