package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.VibrationListenerHolder;
import net.arkadiyhimself.fantazia.api.type.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record EntityMadeSoundS2C(int id) implements IPacket {

    public static final CustomPacketPayload.Type<EntityMadeSoundS2C> TYPE = new Type<>(Fantazia.res("data_attachment_modify.entity_made_sound"));

    public static final StreamCodec<ByteBuf, EntityMadeSoundS2C> CODEC = StreamCodec.composite(ByteBufCodecs.INT, EntityMadeSoundS2C::id, EntityMadeSoundS2C::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) return;
            Entity entity = Minecraft.getInstance().level.getEntity(id);
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (!(entity instanceof LivingEntity livingEntity) || localPlayer == null) return;
            PlayerAbilityGetter.acceptConsumer(localPlayer, VibrationListenerHolder.class, vibrationListen -> vibrationListen.madeSound(livingEntity));
        });
    }
}
