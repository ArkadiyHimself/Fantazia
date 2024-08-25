package net.arkadiyhimself.fantazia.networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class SwingHandS2C implements IPacket {
    private final InteractionHand hand;

    public SwingHandS2C(InteractionHand hand) {
        this.hand = hand;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        if (Minecraft.getInstance().player == null) return;
        context.enqueueWork(() -> Minecraft.getInstance().player.swing(hand));
        context.setPacketHandled(true);
    }
    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeEnum(hand);
    }
    public static SwingHandS2C read(FriendlyByteBuf packetBuf) {
        return new SwingHandS2C(packetBuf.readEnum(InteractionHand.class));
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, SwingHandS2C.class, SwingHandS2C::read);
    }
}
