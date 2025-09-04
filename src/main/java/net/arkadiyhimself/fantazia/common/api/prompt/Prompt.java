package net.arkadiyhimself.fantazia.common.api.prompt;

import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZGameRules;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Supplier;

public record Prompt(ResourceLocation id, String title, String text, ResourceLocation sprite, List<Supplier<Object>> forTitle, List<Supplier<Object>> forText) {

    public static final StreamCodec<RegistryFriendlyByteBuf, Prompt> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, Prompt::id,
            Prompts::getPrompt
    );

    public void maybePromptPlayer(ServerPlayer serverPlayer) {
        if (serverPlayer.getData(FTZAttachmentTypes.USED_PROMPTS).contains(this) || !serverPlayer.serverLevel().getGameRules().getBoolean(FTZGameRules.PROMPTS)) return;
        IPacket.promptPlayer(serverPlayer, this);
    }

    public void noLongerNeeded(ServerPlayer player) {
        if (!player.getData(FTZAttachmentTypes.USED_PROMPTS).contains(this)) player.getData(FTZAttachmentTypes.USED_PROMPTS).add(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof Prompt prompt && prompt.id.equals(this.id);
    }
}
