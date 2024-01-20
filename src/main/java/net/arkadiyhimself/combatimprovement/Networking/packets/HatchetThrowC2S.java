package net.arkadiyhimself.combatimprovement.Networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.Entities.HatchetEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class HatchetThrowC2S implements IPacket {
    private final ItemStack stack;
    public HatchetThrowC2S(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                ServerPlayer player = context.getSender();
                ServerLevel pLevel = (ServerLevel) player.level();
                stack.hurtAndBreak(1, player, (p_43388_) -> p_43388_.broadcastBreakEvent(player.getUsedItemHand()));

                HatchetEntity hatchetEntity = new HatchetEntity(pLevel, player, stack.copy());

                hatchetEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                if (player.getAbilities().instabuild) {
                    hatchetEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                } else {
                    hatchetEntity.pickup = AbstractArrow.Pickup.ALLOWED;
                }
                pLevel.addFreshEntity(hatchetEntity);
                pLevel.playSound(null, hatchetEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeItemStack(stack, false);
    }
    public static HatchetThrowC2S read(FriendlyByteBuf packetBuf) {
        return new HatchetThrowC2S(packetBuf.readItem());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, HatchetThrowC2S.class, HatchetThrowC2S::read);
    }
}
