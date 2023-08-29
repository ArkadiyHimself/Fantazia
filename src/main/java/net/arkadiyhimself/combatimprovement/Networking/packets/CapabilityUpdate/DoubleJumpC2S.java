package net.arkadiyhimself.combatimprovement.Networking.packets.CapabilityUpdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class DoubleJumpC2S implements IPacket {
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) { return; }
            UsefulMethods.Abilities.doubleJump(context.getSender());
            AttachDJump.get(context.getSender()).ifPresent(dJump -> {
                dJump.canDJump = false;
                dJump.updateTracking();
            });
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static DoubleJumpC2S read(FriendlyByteBuf packetBuf) {
        return new DoubleJumpC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, DoubleJumpC2S.class, DoubleJumpC2S::read);
    }
}
