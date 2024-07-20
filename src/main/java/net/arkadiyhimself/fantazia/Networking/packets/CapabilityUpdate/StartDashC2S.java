package net.arkadiyhimself.fantazia.Networking.packets.CapabilityUpdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.Dash;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class StartDashC2S implements IPacket{
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);

            Vec3 vec3 = WhereMagicHappens.Abilities.calculateDashHorizontalVelocity(player, 1.8f, true);

            abilityManager.getAbility(Dash.class).ifPresent(dash -> dash.beginDash(vec3));
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static StartDashC2S read(FriendlyByteBuf packetBuf) {
        return new StartDashC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, StartDashC2S.class, StartDashC2S::read);
    }
}
