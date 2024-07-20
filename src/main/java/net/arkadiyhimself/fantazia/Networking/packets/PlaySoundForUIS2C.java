package net.arkadiyhimself.fantazia.Networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class PlaySoundForUIS2C implements IPacket {
    private SoundEvent sound;
    public PlaySoundForUIS2C(SoundEvent event) {
        sound = event;
    }
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (sound == null) { return; }
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1f, 1f));
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        sound.writeToNetwork(packetBuf);
    }
    public static PlaySoundForUIS2C read(FriendlyByteBuf packetBuf) {
        return new PlaySoundForUIS2C(SoundEvent.readFromNetwork(packetBuf));
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, PlaySoundForUIS2C.class, PlaySoundForUIS2C::read);
    }
}
