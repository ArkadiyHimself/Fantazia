package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HatchetStuckSC2(int id, ItemStack stack) implements IPacket {

    public static final Type<HatchetStuckSC2> TYPE = new Type<>(Fantazia.location("stuff.hatchet_stuck"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HatchetStuckSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HatchetStuckSC2::id,
            ItemStack.STREAM_CODEC, HatchetStuckSC2::stack,
            HatchetStuckSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        StuffHandlers.hatchetStuck(id, stack);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
