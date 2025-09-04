package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompt;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UsedPromptC2S(Prompt prompt) implements IPacket {

    public static final CustomPacketPayload.Type<UsedPromptC2S> TYPE = new CustomPacketPayload.Type<>(Fantazia.location("stuff.used_prompt"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UsedPromptC2S> CODEC = StreamCodec.composite(
            Prompt.STREAM_CODEC, UsedPromptC2S::prompt,
            UsedPromptC2S::new
    );

    @Override
    public void handle(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) StuffHandlers.usedPrompt(serverPlayer, prompt);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
