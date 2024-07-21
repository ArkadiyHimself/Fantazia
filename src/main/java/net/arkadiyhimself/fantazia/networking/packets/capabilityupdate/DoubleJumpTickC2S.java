package net.arkadiyhimself.fantazia.networking.packets.capabilityupdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.Abilities.DoubleJump;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class DoubleJumpTickC2S implements IPacket {
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
            abilityManager.getAbility(DoubleJump.class).ifPresent(DoubleJump::buttonRelease);
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static DoubleJumpTickC2S read(FriendlyByteBuf packetBuf) {
        return new DoubleJumpTickC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, DoubleJumpTickC2S.class, DoubleJumpTickC2S::read);
    }
}
