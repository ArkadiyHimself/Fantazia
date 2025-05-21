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

public record LivingEntityAttachmentTickSyncSC2(int id, CompoundTag tag) implements IAttachmentSync {

    public static final Type<LivingEntityAttachmentTickSyncSC2> TYPE = new Type<>(Fantazia.res("attachment_syncing.living_entity_tick"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LivingEntityAttachmentTickSyncSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LivingEntityAttachmentTickSyncSC2::id,
            ByteBufCodecs.COMPOUND_TAG, LivingEntityAttachmentTickSyncSC2::tag,
            LivingEntityAttachmentTickSyncSC2::new
    );

    public static LivingEntityAttachmentTickSyncSC2 build(LivingEntity livingEntity) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("living_effect_manager", livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).serializeTick());
        compoundTag.put("living_data_manager", livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).serializeTick());
        return new LivingEntityAttachmentTickSyncSC2(livingEntity.getId(), compoundTag);
    }

    @Override
    public void handle(IPayloadContext context) {
        syncTickLivingEntityAttachments(id, tag);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
