package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LivingEffectUpdateS2C(CompoundTag tag, int id, boolean onTick) implements IPacket {

    public static final CustomPacketPayload.Type<LivingEffectUpdateS2C> TYPE = new Type<>(Fantazia.res("data_attachment_update.living_effect"));

    public static final StreamCodec<ByteBuf, LivingEffectUpdateS2C> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, LivingEffectUpdateS2C::tag,
            ByteBufCodecs.INT, LivingEffectUpdateS2C::id,
            ByteBufCodecs.BOOL, LivingEffectUpdateS2C::onTick,
            LivingEffectUpdateS2C::new);

    public LivingEffectUpdateS2C(LivingEntity livingEntity, boolean onTick) {
        this(onTick ? livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).serializeTick() : livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).syncSerialize(), livingEntity.getId(), onTick);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentSyncingHandlers.livingEffect(tag, id, onTick));
    }
}
