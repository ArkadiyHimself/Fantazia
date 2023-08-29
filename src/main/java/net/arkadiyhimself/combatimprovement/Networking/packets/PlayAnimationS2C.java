package net.arkadiyhimself.combatimprovement.Networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class PlayAnimationS2C implements IPacket {
    private String animation;
    public PlayAnimationS2C(String animation) {
        this.animation = animation;
    }
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null && AttachDataSync.getUnwrap(Minecraft.getInstance().player) != null) {
                if (animation.isEmpty()) {
                    animation = null;
                }
                UsefulMethods.Abilities.animatePlayer(Minecraft.getInstance().player, animation);
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeComponent(Component.translatable(animation));
    }
    public static PlayAnimationS2C read(FriendlyByteBuf packetBuf) {
        String anim = packetBuf.readComponent().getString();
        return new PlayAnimationS2C(anim);
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, PlayAnimationS2C.class, PlayAnimationS2C::read);
    }
}
