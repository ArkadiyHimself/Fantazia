package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompt;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PromptPlayerSC2(Prompt prompt) implements IPacket {

    public static final CustomPacketPayload.Type<PromptPlayerSC2> TYPE = new Type<>(Fantazia.location("stuff.prompt_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PromptPlayerSC2> CODEC = StreamCodec.composite(
            Prompt.STREAM_CODEC, PromptPlayerSC2::prompt,
            PromptPlayerSC2::new
    );

    @Override
    public void handle(IPayloadContext context) {
        StuffHandlers.promptPlayer(prompt);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
