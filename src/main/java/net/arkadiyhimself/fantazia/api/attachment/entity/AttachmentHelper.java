package net.arkadiyhimself.fantazia.api.attachment.entity;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.LivingDataUpdateS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.LivingEffectUpdateS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.PlayerAbilityUpdateS2C;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class AttachmentHelper {

    public static void tickAttachments(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            LivingEffectGetter.getUnwrap(livingEntity).tick();
            LivingDataGetter.getUnwrap(livingEntity).tick();

            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new LivingEffectUpdateS2C(livingEntity));
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new LivingDataUpdateS2C(livingEntity));
        }

        if (entity instanceof Player player) {
            PlayerAbilityGetter.getUnwrap(player).tick();
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayerAbilityUpdateS2C(player));
        }
    }
}
