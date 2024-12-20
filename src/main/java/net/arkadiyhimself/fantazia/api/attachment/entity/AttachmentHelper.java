package net.arkadiyhimself.fantazia.api.attachment.entity;

import net.arkadiyhimself.fantazia.packets.attachment_syncing.LivingDataUpdateS2C;
import net.arkadiyhimself.fantazia.packets.attachment_syncing.LivingEffectUpdateS2C;
import net.arkadiyhimself.fantazia.packets.attachment_syncing.PlayerAbilityUpdateS2C;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class AttachmentHelper {

    public static void tickAttachments(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).tick();
            livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).tick();

            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new LivingEffectUpdateS2C(livingEntity));
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new LivingDataUpdateS2C(livingEntity));
        }

        if (entity instanceof Player player) {
            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).tick();
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayerAbilityUpdateS2C(player));
        }
    }
}
