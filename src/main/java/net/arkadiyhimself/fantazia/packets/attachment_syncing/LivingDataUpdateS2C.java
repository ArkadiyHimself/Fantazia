package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LivingDataUpdateS2C(CompoundTag compoundTag, int id) implements IPacket {
    public static final CustomPacketPayload.Type<LivingDataUpdateS2C> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("data_attachment_update.living_data"));

    public static final StreamCodec<ByteBuf, LivingDataUpdateS2C> CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, LivingDataUpdateS2C::compoundTag, ByteBufCodecs.INT, LivingDataUpdateS2C::id, LivingDataUpdateS2C::new);

    public LivingDataUpdateS2C(LivingEntity livingEntity) {
        this(livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).syncSerialize(), livingEntity.getId());
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) return;
            Entity entity = clientLevel.getEntity(id);
            if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.hasData(FTZAttachmentTypes.DATA_MANAGER)) return;
            livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).syncDeserialize(compoundTag);
        });
    }
}
