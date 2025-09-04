package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SwingHandS2C(InteractionHand hand) implements IPacket {

    public static final CustomPacketPayload.Type<SwingHandS2C> TYPE = new Type<>(Fantazia.location("stuff.swing_hand"));

    public static final StreamCodec<FriendlyByteBuf, SwingHandS2C> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(InteractionHand.class),
            SwingHandS2C::hand, SwingHandS2C::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> StuffHandlers.swingHand(hand));
    }
}
