package net.arkadiyhimself.fantazia.packets.commands;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BuildSpellTooltipSC2(ResourceLocation id) implements IPacket {

    public static final CustomPacketPayload.Type<BuildSpellTooltipSC2> TYPE = new Type<>(Fantazia.res("commands.build_spell_tooltip"));

    public static final StreamCodec<ByteBuf, BuildSpellTooltipSC2> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, BuildSpellTooltipSC2::id,
            BuildSpellTooltipSC2::new);

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> CommandsHandlers.buildSpellTooltip(id));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
