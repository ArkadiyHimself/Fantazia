package net.arkadiyhimself.fantazia.Networking.packets.CapabilityUpdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.RenderingValues;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;

public class SoundExpiredS2C implements IPacket {
    @Nullable
    private final LivingEntity entity;
    public SoundExpiredS2C(@Nullable LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager == null) return;
            abilityManager.getAbility(RenderingValues.class).ifPresent(renderingValues -> renderingValues.soundExpired(entity));
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(entity.getId());
    }
    public static SoundExpiredS2C read(FriendlyByteBuf packetBuf) {
        LivingEntity livingEntity = null;
        if (Minecraft.getInstance().level != null) {
            livingEntity = (LivingEntity) Minecraft.getInstance().level.getEntity(packetBuf.readInt());
        }
        return new SoundExpiredS2C(livingEntity);
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, SoundExpiredS2C.class, SoundExpiredS2C::read);
    }
}
