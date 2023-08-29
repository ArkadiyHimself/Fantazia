package net.arkadiyhimself.combatimprovement.Networking.packets.CapabilityUpdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class DJumpStartTickC2S implements IPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) { return; }
            AttachDJump.get(context.getSender()).ifPresent(dJump -> {
                dJump.startTick = true;
                dJump.updateTracking();
            });
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static DJumpStartTickC2S read(FriendlyByteBuf packetBuf) {
        return new DJumpStartTickC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, DJumpStartTickC2S.class, DJumpStartTickC2S::read);
    }
}
