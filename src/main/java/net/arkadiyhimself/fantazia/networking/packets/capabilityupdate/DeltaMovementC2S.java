package net.arkadiyhimself.fantazia.networking.packets.capabilityupdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities.RenderingValues;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class DeltaMovementC2S implements IPacket {
    private final Vec3 movement;
    public DeltaMovementC2S(Vec3 vec3) {
        this.movement = vec3;

    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
            abilityManager.getAbility(RenderingValues.class).ifPresent(renderingValues -> renderingValues.deltaMovement = movement);
        });
        context.setPacketHandled(true);
    }
    public static DeltaMovementC2S read(FriendlyByteBuf packetBuf) {
        return new DeltaMovementC2S(new Vec3(packetBuf.readDouble(), packetBuf.readDouble(), packetBuf.readDouble()));
    }
    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeDouble(movement.x());
        packetBuf.writeDouble(movement.y());
        packetBuf.writeDouble(movement.z());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, DeltaMovementC2S.class, DeltaMovementC2S::read);
    }
}
