package net.arkadiyhimself.fantazia.networking.packets.capabilityupdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities.MeleeBlock;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class StartedBlockingC2S implements IPacket {
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
            abilityManager.getAbility(MeleeBlock.class).ifPresent(MeleeBlock::startBlocking);
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static StartedBlockingC2S read(FriendlyByteBuf packetBuf) {
        return new StartedBlockingC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, StartedBlockingC2S.class, StartedBlockingC2S::read);
    }
}
