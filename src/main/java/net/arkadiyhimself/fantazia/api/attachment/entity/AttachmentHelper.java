package net.arkadiyhimself.fantazia.api.attachment.entity;

import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.TickingIntegerHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
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
        if (entity.level().isClientSide()) {
            entity.getData(FTZAttachmentTypes.REFLECT_RENDER_VALUES).tick();
        }

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).tick();
            livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).tick();

            if (!entity.level().isClientSide()) PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new LivingEffectUpdateS2C(livingEntity,true));
            if (!entity.level().isClientSide()) PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new LivingDataUpdateS2C(livingEntity));

            livingEntity.getData(FTZAttachmentTypes.TRANQUILIZE_DAMAGE_TICKS).tick();
            TickingIntegerHolder ancientFlameTicks = livingEntity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS);
            ancientFlameTicks.tick();
            if (ancientFlameTicks.value() > 0 && (ancientFlameTicks.value() % 20) == 0) LevelAttributesHelper.hurtEntity(livingEntity, 1.5f, DamageSourcesHolder::ancientBurning);
        }

        if (entity instanceof Player player) {
            player.getData(FTZAttachmentTypes.REWIND_PARAMETERS).tick();
            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).tick();
            if (!player.level().isClientSide()) PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayerAbilityUpdateS2C(player));

            player.getData(FTZAttachmentTypes.MURASAMA_TAUNT_TICKS).tick();
        }

    }
}
