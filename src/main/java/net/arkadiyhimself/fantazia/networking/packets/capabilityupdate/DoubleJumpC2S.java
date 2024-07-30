package net.arkadiyhimself.fantazia.networking.packets.capabilityupdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.DoubleJump;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class DoubleJumpC2S implements IPacket {
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
            if (abilityManager == null) return;
            abilityManager.getAbility(DoubleJump.class).ifPresent(DoubleJump::tryToJump);
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