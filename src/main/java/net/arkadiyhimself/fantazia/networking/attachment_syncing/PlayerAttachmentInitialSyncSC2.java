package net.arkadiyhimself.fantazia.networking.attachment_syncing;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayerAttachmentInitialSyncSC2(int id, CompoundTag tag) implements IAttachmentSync {

    public static final Type<PlayerAttachmentInitialSyncSC2> TYPE = new Type<>(Fantazia.location("attachment_syncing.player_initial"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerAttachmentInitialSyncSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PlayerAttachmentInitialSyncSC2::id,
            ByteBufCodecs.COMPOUND_TAG, PlayerAttachmentInitialSyncSC2::tag,
            PlayerAttachmentInitialSyncSC2::new
    );

    public static PlayerAttachmentInitialSyncSC2 build(ServerPlayer serverPlayer) {
        CompoundTag tag = new CompoundTag();
        tag.put("living_effect_manager", serverPlayer.getData(FTZAttachmentTypes.EFFECT_MANAGER).serializeInitial());
        tag.put("living_data_manager", serverPlayer.getData(FTZAttachmentTypes.DATA_MANAGER).serializeInitial());
        tag.putInt("layered_barrier_layers", serverPlayer.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS));
        tag.putFloat("barrier_health", serverPlayer.getData(FTZAttachmentTypes.BARRIER_HEALTH));
        tag.putString("amplification_tab", serverPlayer.getData(FTZAttachmentTypes.LAST_SELECTED_AMPLIFICATION_TAB).ident());

        tag.put("ability_manager", serverPlayer.getData(FTZAttachmentTypes.ABILITY_MANAGER).serializeInitial());
        tag.put("wanderers_spirit_location", serverPlayer.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION).serialize());
        tag.putInt("all_in_previous_outcome", serverPlayer.getData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME));
        tag.putBoolean("wall_climbing_unlocked", serverPlayer.getData(FTZAttachmentTypes.WALL_CLIMBING_UNLOCKED));
        tag.putBoolean("wall_climbing_cobweb", serverPlayer.getData(FTZAttachmentTypes.WALL_CLIMBING_COBWEB));
        tag.put("passive_barrier", serverPlayer.getData(FTZAttachmentTypes.PASSIVE_BARRIER_PERK).serializeInitial());
        tag.putBoolean("hurt_by_falling", serverPlayer.getData(FTZAttachmentTypes.HURT_BY_FALLING_PERK));
        return new PlayerAttachmentInitialSyncSC2(serverPlayer.getId(), tag);
    }

    @Override
    public void handle(IPayloadContext context) {
        syncInitialPlayerAttachments(id, tag);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
