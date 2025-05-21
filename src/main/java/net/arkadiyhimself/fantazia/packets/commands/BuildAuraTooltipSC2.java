package net.arkadiyhimself.fantazia.packets.commands;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BuildAuraTooltipSC2(ResourceLocation id) implements IPacket {

    public static final CustomPacketPayload.Type<BuildAuraTooltipSC2> TYPE = new CustomPacketPayload.Type<>(Fantazia.res("commands.build_aura_tooltip"));

    public static final StreamCodec<ByteBuf, BuildAuraTooltipSC2> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, BuildAuraTooltipSC2::id,
            BuildAuraTooltipSC2::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> CommandsHandlers.buildAuraTooltip(id));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
