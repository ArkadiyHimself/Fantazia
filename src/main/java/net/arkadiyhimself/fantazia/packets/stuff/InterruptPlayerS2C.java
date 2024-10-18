package net.arkadiyhimself.fantazia.packets.stuff;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.type.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record InterruptPlayerS2C() implements IPacket {

    public static final CustomPacketPayload.Type<InterruptPlayerS2C> TYPE = new Type<>(Fantazia.res("stuff.interrupt_player"));

    public static final StreamCodec<ByteBuf, InterruptPlayerS2C> CODEC = StreamCodec.unit(new InterruptPlayerS2C());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null) return;
            if (!((Minecraft.getInstance().screen) instanceof ChatScreen)) Minecraft.getInstance().player.clientSideCloseContainer();
            Minecraft.getInstance().player.stopUsingItem();
            Minecraft.getInstance().player.stopSleeping();
            Minecraft.getInstance().options.keyAttack.setDown(false);
        });
    }
}
