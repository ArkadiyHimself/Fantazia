package net.arkadiyhimself.fantazia.networking.packets.stuff;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.type.IPacket;
import net.arkadiyhimself.fantazia.client.models.PlayerAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayAnimationS2C(String animation) implements IPacket {

    public static final CustomPacketPayload.Type<PlayAnimationS2C> TYPE = new Type<>(Fantazia.res("stuff.play_animation"));

    public static final StreamCodec<ByteBuf, PlayAnimationS2C> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, PlayAnimationS2C::animation, PlayAnimationS2C::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        if (Minecraft.getInstance().player == null) return;
        context.enqueueWork(() -> PlayerAnimations.animatePlayer(Minecraft.getInstance().player, animation.isEmpty() ? null : animation));
    }
}
