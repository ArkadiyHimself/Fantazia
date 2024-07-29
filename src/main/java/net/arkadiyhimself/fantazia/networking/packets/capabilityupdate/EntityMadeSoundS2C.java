package net.arkadiyhimself.fantazia.networking.packets.capabilityupdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities.VibrationListen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class EntityMadeSoundS2C implements IPacket {
    private final LivingEntity entity;
    public EntityMadeSoundS2C(LivingEntity entity) {
        this.entity = entity;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager == null) return;
            abilityManager.getAbility(VibrationListen.class).ifPresent(vibrationListen -> vibrationListen.madeSound(entity));

        });
        context.setPacketHandled(true);
    }
    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(entity.getId());
    }
    public static EntityMadeSoundS2C read(FriendlyByteBuf packetBuf) {
        LivingEntity livingEntity = (LivingEntity) Minecraft.getInstance().level.getEntity(packetBuf.readInt());
        return new EntityMadeSoundS2C(livingEntity);
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, EntityMadeSoundS2C.class, EntityMadeSoundS2C::read);
    }
}
