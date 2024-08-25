package net.arkadiyhimself.fantazia.networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class KickOutOfGuiS2C implements IPacket {
    public void handle(NetworkEvent.Context context) {
        if (Minecraft.getInstance().player == null) return;
        context.enqueueWork(() -> {
            Minecraft.getInstance().player.closeContainer();
            Minecraft.getInstance().player.stopUsingItem();
        });
        context.setPacketHandled(true);
    }
    @Override
    public void write(FriendlyByteBuf packetBuf) {}
    public static KickOutOfGuiS2C read(FriendlyByteBuf packetBuf) {
        return new KickOutOfGuiS2C();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, KickOutOfGuiS2C.class, KickOutOfGuiS2C::read);
    }
}
