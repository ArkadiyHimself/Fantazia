package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.screen.AmplifyResource;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AmplificationMenuEnoughResourcesSC2(AmplifyResource enoughWisdom, AmplifyResource enoughSubstance) implements IPacket {

    public static final CustomPacketPayload.Type<AmplificationMenuEnoughResourcesSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("stuff.amplification_menu_enough_resources"));


    public static final StreamCodec<RegistryFriendlyByteBuf, AmplificationMenuEnoughResourcesSC2> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(AmplifyResource.class), AmplificationMenuEnoughResourcesSC2::enoughWisdom,
            NeoForgeStreamCodecs.enumCodec(AmplifyResource.class), AmplificationMenuEnoughResourcesSC2::enoughSubstance,
            AmplificationMenuEnoughResourcesSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        StuffHandlers.amplificationMenuEnoughResources(enoughWisdom, enoughSubstance);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
