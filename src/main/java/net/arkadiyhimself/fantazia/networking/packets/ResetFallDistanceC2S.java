package net.arkadiyhimself.fantazia.networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class ResetFallDistanceC2S implements IPacket {
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                context.getSender().fallDistance = -2f;
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
    }
    public static ResetFallDistanceC2S read(FriendlyByteBuf packetBuf) {
        return new ResetFallDistanceC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, ResetFallDistanceC2S.class, ResetFallDistanceC2S::read);
    }
}
