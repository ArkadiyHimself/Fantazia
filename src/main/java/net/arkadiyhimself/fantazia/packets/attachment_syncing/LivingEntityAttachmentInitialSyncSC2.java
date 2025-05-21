package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LivingEntityAttachmentInitialSyncSC2(int id, CompoundTag tag) implements IAttachmentSync {

    public static final CustomPacketPayload.Type<LivingEntityAttachmentInitialSyncSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("attachment_syncing.living_entity_initial"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LivingEntityAttachmentInitialSyncSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LivingEntityAttachmentInitialSyncSC2::id,
            ByteBufCodecs.COMPOUND_TAG, LivingEntityAttachmentInitialSyncSC2::tag,
            LivingEntityAttachmentInitialSyncSC2::new
    );

    public static LivingEntityAttachmentInitialSyncSC2 build(LivingEntity livingEntity) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("living_effect_manager", livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).serializeInitial());
        compoundTag.put("living_data_manager", livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).serializeInitial());
        compoundTag.putInt("layered_barrier_layers", livingEntity.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS));
        compoundTag.putFloat("barrier_health", livingEntity.getData(FTZAttachmentTypes.BARRIER_HEALTH));
        return new LivingEntityAttachmentInitialSyncSC2(livingEntity.getId(), compoundTag);
    }

    @Override
    public void handle(IPayloadContext context) {
        syncInitialLivingEntityAttachments(id, tag);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
