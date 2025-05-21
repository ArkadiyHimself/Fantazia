package net.arkadiyhimself.fantazia.packets.attachment_syncing;

import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.LocationHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.SimpleMobEffectSyncHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public interface IAttachmentSync extends IPacket {

    default void syncInitialLivingEntityAttachments(int id, CompoundTag compoundTag) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(id) instanceof LivingEntity livingEntity)) return;
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).deserializeInitial(compoundTag.getCompound("living_effect_manager"));
        livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).deserializeInitial(compoundTag.getCompound("living_data_manager"));
        livingEntity.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, compoundTag.getInt("layered_barrier_layers"));
        livingEntity.setData(FTZAttachmentTypes.BARRIER_HEALTH, compoundTag.getFloat("barrier_health"));
    }

    default void syncTickLivingEntityAttachments(int id, CompoundTag compoundTag) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(id) instanceof LivingEntity livingEntity)) return;
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).deserializeTick(compoundTag.getCompound("living_effect_manager"));
        livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).deserializeTick(compoundTag.getCompound("living_data_manager"));
    }

    default void syncInitialPlayerAttachments(int id, CompoundTag compoundTag) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(id) instanceof LocalPlayer player)) return;
        player.getData(FTZAttachmentTypes.EFFECT_MANAGER).deserializeInitial(compoundTag.getCompound("living_effect_manager"));
        player.getData(FTZAttachmentTypes.DATA_MANAGER).deserializeInitial(compoundTag.getCompound("living_data_manager"));
        player.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, compoundTag.getInt("layered_barrier_layers"));
        player.setData(FTZAttachmentTypes.BARRIER_HEALTH, compoundTag.getFloat("barrier_health"));

        player.getData(FTZAttachmentTypes.ABILITY_MANAGER).deserializeInitial(compoundTag.getCompound("ability_manager"));
        player.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION).deserialize(compoundTag.getCompound("wanderers_spirit_location"));
        player.setData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME, compoundTag.getInt("all_in_previous_outcome"));
        player.setData(FTZAttachmentTypes.WALL_CLIMBING_UNLOCKED, compoundTag.getBoolean("wall_climbing_unlocked"));
        player.setData(FTZAttachmentTypes.WALL_CLIMBING_COBWEB, compoundTag.getBoolean("wall_climbing_cobweb"));
    }

    default void syncTickPlayerAttachments(int id, CompoundTag compoundTag) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(id) instanceof LocalPlayer localPlayer)) return;
        localPlayer.getData(FTZAttachmentTypes.EFFECT_MANAGER).deserializeTick(compoundTag.getCompound("living_effect_manager"));
        localPlayer.getData(FTZAttachmentTypes.DATA_MANAGER).deserializeTick(compoundTag.getCompound("living_data_manager"));

        localPlayer.getData(FTZAttachmentTypes.ABILITY_MANAGER).deserializeTick(compoundTag.getCompound("ability_manager"));
    }

    default void levelAttributes(CompoundTag tag) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        clientLevel.getData(FTZAttachmentTypes.LEVEL_ATTRIBUTES).deserializeInitial(tag);
    }

    default void simpleMobEffectSyncing(CompoundTag tag, int id) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null || !(clientLevel.getEntity(id) instanceof LivingEntity livingEntity)) return;
        LivingEffectHelper.acceptConsumer(livingEntity, SimpleMobEffectSyncHolder.class, holder -> holder.deserializeInitial(tag));
    }

    static void onEntityJoinLevel(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, PlayerAttachmentInitialSyncSC2.build(serverPlayer));
        }
        else if (entity instanceof LivingEntity livingEntity) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, LivingEntityAttachmentInitialSyncSC2.build(livingEntity));
        }
    }

    static void onEntityTracked(Entity entity, ServerPlayer player) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(player, PlayerAttachmentInitialSyncSC2.build(serverPlayer));
        }
        else if (entity instanceof LivingEntity livingEntity) {
            PacketDistributor.sendToPlayer(player, LivingEntityAttachmentInitialSyncSC2.build(livingEntity));
        }
    }

    static void onEntityTick(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, PlayerAttachmentTickSyncSC2.build(serverPlayer));
        }
        else if (entity instanceof LivingEntity livingEntity) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, LivingEntityAttachmentTickSyncSC2.build(livingEntity));
        }
    }

    static void updateLevelAttributes(ServerLevel level) {
        PacketDistributor.sendToAllPlayers(new LevelAttributesUpdateS2C(LevelAttributesHelper.getUnwrap(level).serializeInitial()));
    }
}
