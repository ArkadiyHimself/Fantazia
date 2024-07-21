package net.arkadiyhimself.fantazia.networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.entities.HatchetEntity;
import net.arkadiyhimself.fantazia.registry.SoundRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class HatchetThrowC2S implements IPacket {
    private final ItemStack stack;
    private int id;
    public HatchetThrowC2S(ItemStack stack, int id) {
        this.stack = stack;
        this.id = id;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                ServerPlayer player = context.getSender();
                ServerLevel pLevel = (ServerLevel) player.level();
                stack.hurtAndBreak(1, player, (p_43388_) -> p_43388_.broadcastBreakEvent(player.getUsedItemHand()));

                HatchetEntity hatchetEntity = new HatchetEntity(pLevel, player, stack.copy());

                hatchetEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.85F, 1.0F);
                hatchetEntity.calculateRotSpeed();
                if (player.getAbilities().instabuild) {
                    hatchetEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                } else {
                    hatchetEntity.pickup = AbstractArrow.Pickup.ALLOWED;
                }
                pLevel.addFreshEntity(hatchetEntity);
                pLevel.playSound(null, hatchetEntity, SoundRegistry.HATCHET_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                if (id > 0 && !player.getAbilities().instabuild) {
                    player.getInventory().removeItem(id, 1);
                }
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeItemStack(stack, false);
        packetBuf.writeInt(id);
    }
    public static HatchetThrowC2S read(FriendlyByteBuf packetBuf) {
        return new HatchetThrowC2S(packetBuf.readItem(), packetBuf.readInt());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, HatchetThrowC2S.class, HatchetThrowC2S::read);
    }
}
