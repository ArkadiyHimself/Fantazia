package net.arkadiyhimself.combatimprovement.Networking.packets.CapabilityUpdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class EntityMadeSoundS2C implements IPacket {
    private final boolean add;
    private final LivingEntity entity;
    public EntityMadeSoundS2C(LivingEntity entity, boolean add) {
        this.entity = entity;
        this.add = add;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            AttachDataSync.get(player).ifPresent(dataSync -> {
                if (add) {
                    dataSync.entityMadeSound(entity);
                } else {
                    dataSync.entitySoundExpired(entity);
                }
            });
        });
        context.setPacketHandled(true);
    }
    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(entity.getId());
        packetBuf.writeBoolean(add);
    }
    public static EntityMadeSoundS2C read(FriendlyByteBuf packetBuf) {
        LivingEntity livingEntity = (LivingEntity) Minecraft.getInstance().level.getEntity(packetBuf.readInt());
        return new EntityMadeSoundS2C(livingEntity, packetBuf.readBoolean());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, EntityMadeSoundS2C.class, EntityMadeSoundS2C::read);
    }
}
