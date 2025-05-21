package net.arkadiyhimself.fantazia.packets.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AddDashStoneProtectorsSC2(int id, List<Integer> protectors) implements IPacket {

    public static final CustomPacketPayload.Type<AddDashStoneProtectorsSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("stuff.add_dashstone_protectors"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddDashStoneProtectorsSC2> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AddDashStoneProtectorsSC2::id,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), AddDashStoneProtectorsSC2::protectors,
            AddDashStoneProtectorsSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> StuffHandlers.addDashStoneProtectors(id, protectors));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
