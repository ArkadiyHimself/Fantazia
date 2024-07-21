package net.arkadiyhimself.fantazia.networking.packets.capabilityupdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class JustDJumpedC2S implements IPacket {
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) { return; }

        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static JustDJumpedC2S read(FriendlyByteBuf packetBuf) {
        return new JustDJumpedC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, JustDJumpedC2S.class, JustDJumpedC2S::read);
    }
}
