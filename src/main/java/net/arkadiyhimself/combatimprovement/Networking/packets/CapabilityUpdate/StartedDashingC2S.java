package net.arkadiyhimself.combatimprovement.Networking.packets.CapabilityUpdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

public class StartedDashingC2S implements IPacket{
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }

            List<Double> x0z = WhereMagicHappens.Abilities.calculateDashHorizontalVelocity(player,1.8f);
            AttachDash.get(player).ifPresent(dash -> dash.startDash(x0z.get(0), x0z.get(1), player));
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static StartedDashingC2S read(FriendlyByteBuf packetBuf) {
        return new StartedDashingC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, StartedDashingC2S.class, StartedDashingC2S::read);
    }
}
