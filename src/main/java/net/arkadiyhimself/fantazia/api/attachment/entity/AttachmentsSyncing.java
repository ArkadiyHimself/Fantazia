package net.arkadiyhimself.fantazia.api.attachment.entity;

import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.TickingIntegerHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.PuppeteeredEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.packets.attachment_syncing.IAttachmentSync;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AttachmentsSyncing {

    public static void tickAttachments(Entity entity) {
        if (entity.level().isClientSide()) {
            entity.getData(FTZAttachmentTypes.REFLECT_RENDER_VALUES).tick();

            float layeredBarColor = entity.getData(FTZAttachmentTypes.LAYERED_BARRIER_COLOR);
            entity.setData(FTZAttachmentTypes.LAYERED_BARRIER_COLOR, Math.max(layeredBarColor - 0.1f, 0f));
            float barColor = entity.getData(FTZAttachmentTypes.BARRIER_COLOR);
            entity.setData(FTZAttachmentTypes.BARRIER_COLOR, Math.max(barColor - 0.1f, 0f));
        } else {
            IAttachmentSync.onEntityTick(entity);
        }

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).tick();
            livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).tick();

            livingEntity.getData(FTZAttachmentTypes.TRANQUILIZE_DAMAGE_TICKS).tick();
            TickingIntegerHolder ancientFlameTicks = livingEntity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS);
            ancientFlameTicks.tick();
            if (ancientFlameTicks.value() > 0 && (ancientFlameTicks.value() % 20) == 0) LevelAttributesHelper.hurtEntity(livingEntity, 1.5f, DamageSourcesHolder::ancientBurning);
        }

        if (entity instanceof Player player) {
            player.getData(FTZAttachmentTypes.REWIND_PARAMETERS).tick();
            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).tick();

            player.getData(FTZAttachmentTypes.MURASAMA_TAUNT_TICKS).tick();
        }
    }
}
