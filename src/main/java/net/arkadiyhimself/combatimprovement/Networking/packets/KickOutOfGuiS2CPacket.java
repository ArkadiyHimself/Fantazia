package net.arkadiyhimself.combatimprovement.Networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public record KickOutOfGuiS2CPacket() implements IPacket {

    public void handle(NetworkEvent.Context context) {
        if (Minecraft.getInstance().player == null) { return; }
        context.enqueueWork(() -> {
            Minecraft.getInstance().player.closeContainer();
            Minecraft.getInstance().player.stopUsingItem();
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {}
    public static KickOutOfGuiS2CPacket read(FriendlyByteBuf packetBuf) {
        return new KickOutOfGuiS2CPacket();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, KickOutOfGuiS2CPacket.class, KickOutOfGuiS2CPacket::read);
    }
}
