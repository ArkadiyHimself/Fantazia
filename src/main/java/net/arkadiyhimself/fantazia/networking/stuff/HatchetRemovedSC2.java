package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HatchetRemovedSC2(int id) implements IPacket {

    public static final Type<HatchetRemovedSC2> TYPE = new Type<>(Fantazia.location("stuff.hatchet_removed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HatchetRemovedSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HatchetRemovedSC2::id,
            HatchetRemovedSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        StuffHandlers.hatchetRemoved(id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
