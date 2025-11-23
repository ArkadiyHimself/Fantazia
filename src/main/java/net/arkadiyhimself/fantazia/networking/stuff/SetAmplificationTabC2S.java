package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.screen.AmplificationTab;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetAmplificationTabC2S(AmplificationTab tab) implements IPacket {

    public static final CustomPacketPayload.Type<SetAmplificationTabC2S> TYPE = new Type<>(Fantazia.location("stuff.set_amplification_tab"));
    public static final StreamCodec<FriendlyByteBuf, SetAmplificationTabC2S> CODEC = StreamCodec.composite(
            AmplificationTab.STREAM_CODEC, SetAmplificationTabC2S::tab,
            SetAmplificationTabC2S::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        StuffHandlers.setAmplificationTab(context, tab);
    }
}
