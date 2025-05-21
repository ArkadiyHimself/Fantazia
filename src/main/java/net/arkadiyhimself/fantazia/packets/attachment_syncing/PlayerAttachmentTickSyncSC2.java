package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayerAttachmentTickSyncSC2(int id, CompoundTag tag) implements IAttachmentSync {

    public static final Type<PlayerAttachmentTickSyncSC2> TYPE = new Type<>(Fantazia.res("attachment_syncing.player_tick"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerAttachmentTickSyncSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PlayerAttachmentTickSyncSC2::id,
            ByteBufCodecs.COMPOUND_TAG, PlayerAttachmentTickSyncSC2::tag,
            PlayerAttachmentTickSyncSC2::new
    );

    public static PlayerAttachmentTickSyncSC2 build(ServerPlayer serverPlayer) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("living_effect_manager", serverPlayer.getData(FTZAttachmentTypes.EFFECT_MANAGER).serializeTick());
        compoundTag.put("living_data_manager", serverPlayer.getData(FTZAttachmentTypes.DATA_MANAGER).serializeTick());

        compoundTag.put("ability_manager", serverPlayer.getData(FTZAttachmentTypes.ABILITY_MANAGER).serializeTick());
        return new PlayerAttachmentTickSyncSC2(serverPlayer.getId(), compoundTag);
    }

    @Override
    public void handle(IPayloadContext context) {
        syncTickPlayerAttachments(id, tag);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
