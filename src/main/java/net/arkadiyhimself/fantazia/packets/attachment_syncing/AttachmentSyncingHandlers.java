package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

interface AttachmentSyncingHandlers {

    static void levelAttributes(CompoundTag tag) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        clientLevel.getData(FTZAttachmentTypes.LEVEL_ATTRIBUTES).syncDeserialize(tag);
    }

    static void livingData(CompoundTag tag, int id) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(id);
        if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.hasData(FTZAttachmentTypes.DATA_MANAGER)) return;
        livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).syncDeserialize(tag);
    }

    static void livingEffect(CompoundTag tag, int id, boolean onTick) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(id);
        if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.hasData(FTZAttachmentTypes.EFFECT_MANAGER)) return;
        if (onTick) entity.getData(FTZAttachmentTypes.EFFECT_MANAGER).deserializeTick(tag);
        else entity.getData(FTZAttachmentTypes.EFFECT_MANAGER).syncDeserialize(tag);
    }

    static void playerAbility(CompoundTag tag, int id) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(id);
        if (!(entity instanceof Player player) || !entity.hasData(FTZAttachmentTypes.ABILITY_MANAGER)) return;
        player.getData(FTZAttachmentTypes.ABILITY_MANAGER).syncDeserialize(tag);
    }
}
