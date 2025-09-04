package net.arkadiyhimself.fantazia.networking.attachment_syncing;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.AurasInstancesHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpdateAuraInstancesS2C(CompoundTag tag) implements IAttachmentSync {

    public static final CustomPacketPayload.Type<UpdateAuraInstancesS2C> TYPE = new Type<>(Fantazia.location("data_attachment_update.update_aura_instances"));

    public static final StreamCodec<ByteBuf, UpdateAuraInstancesS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, UpdateAuraInstancesS2C::tag,
            UpdateAuraInstancesS2C::new
    );

    public static UpdateAuraInstancesS2C create(ServerLevel serverLevel) {
        AurasInstancesHolder holder = LevelAttributesHelper.takeHolder(serverLevel, AurasInstancesHolder.class);
        if (holder == null) return new UpdateAuraInstancesS2C(new CompoundTag());
        else return new UpdateAuraInstancesS2C(holder.serializeInitial());
    }

    @Override
    public void handle(IPayloadContext context) {
        updateAuraInstances(tag);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
