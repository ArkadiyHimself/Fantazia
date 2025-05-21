package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record EffectSyncSC2(int id, ResourceLocation effectLocation, boolean present) implements IPacket {

    public static final Type<EffectSyncSC2> TYPE = new Type<>(Fantazia.res("data_attachment_modify.simple_effect_syncing"));

    public static final StreamCodec<ByteBuf, EffectSyncSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EffectSyncSC2::id,
            ResourceLocation.STREAM_CODEC, EffectSyncSC2::effectLocation,
            ByteBufCodecs.BOOL, EffectSyncSC2::present,
            EffectSyncSC2::new);

    public EffectSyncSC2(LivingEntity entity, MobEffect mobEffect, boolean present) {
        this(entity.getId(), BuiltInRegistries.MOB_EFFECT.getKey(mobEffect), present);
    }


    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AttachmentModifyHandlers.simpleEffectSyncing(id, effectLocation, present));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
