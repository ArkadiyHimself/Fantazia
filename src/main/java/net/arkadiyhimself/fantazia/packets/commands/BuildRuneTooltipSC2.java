package net.arkadiyhimself.fantazia.packets.commands;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BuildRuneTooltipSC2(ResourceLocation id) implements IPacket {

    public static final CustomPacketPayload.Type<BuildRuneTooltipSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("commands.build_rune_tooltip"));

    public static final StreamCodec<ByteBuf, BuildRuneTooltipSC2> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, BuildRuneTooltipSC2::id,
            BuildRuneTooltipSC2::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> CommandsHandlers.buildRuneTooltip(id));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
