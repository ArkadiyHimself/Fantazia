package net.arkadiyhimself.combatimprovement.Networking.packets.CapabilityUpdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.Blocking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class StartedBlockingC2S implements IPacket {
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) { return; }
            AttachBlocking.get(context.getSender()).ifPresent(blocking -> blocking.startBlocking(context.getSender()));
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static StartedBlockingC2S read(FriendlyByteBuf packetBuf) {
        return new StartedBlockingC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, StartedBlockingC2S.class, StartedBlockingC2S::read);
    }
}
