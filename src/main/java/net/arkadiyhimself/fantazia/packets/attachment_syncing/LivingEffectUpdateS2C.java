package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.type.IPacket;
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

public record LivingEffectUpdateS2C(CompoundTag compoundTag, int id) implements IPacket {

    public static final CustomPacketPayload.Type<LivingEffectUpdateS2C> TYPE = new Type<>(Fantazia.res("data_attachment_update.living_effect"));

    public static final StreamCodec<ByteBuf, LivingEffectUpdateS2C> CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, LivingEffectUpdateS2C::compoundTag, ByteBufCodecs.INT, LivingEffectUpdateS2C::id, LivingEffectUpdateS2C::new);

    public LivingEffectUpdateS2C(LivingEntity livingEntity) {
        this(livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).syncSerialize(), livingEntity.getId());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) return;
            Entity entity = clientLevel.getEntity(id);
            if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.hasData(FTZAttachmentTypes.EFFECT_MANAGER)) return;
            entity.getData(FTZAttachmentTypes.EFFECT_MANAGER).syncDeserialize(compoundTag);
        });
    }
}
