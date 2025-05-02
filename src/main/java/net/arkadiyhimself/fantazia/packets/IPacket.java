package net.arkadiyhimself.fantazia.packets;

import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.packets.attachment_modify.ReflectLayerActivateSC2;
import net.arkadiyhimself.fantazia.packets.attachment_modify.SimpleEffectSyncingSC2;
import net.arkadiyhimself.fantazia.packets.attachment_syncing.LevelAttributesUpdateS2C;
import net.arkadiyhimself.fantazia.packets.stuff.AnimatePlayerSC2;
import net.arkadiyhimself.fantazia.packets.stuff.PlaySoundForUIS2C;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IPacket extends CustomPacketPayload {

    void handle(IPayloadContext context);

    static void mirrorReflect(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new ReflectLayerActivateSC2());
    }

    static void levelUpdate(Level level) {
        PacketDistributor.sendToAllPlayers(new LevelAttributesUpdateS2C(LevelAttributesHelper.getUnwrap(level).syncSerialize()));
    }

    static void levelUpdate(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new LevelAttributesUpdateS2C(LevelAttributesHelper.getUnwrap(serverPlayer.level()).syncSerialize()));
    }

    static void soundForUI(ServerPlayer serverPlayer, SoundEvent soundEvent, float pitch, float volume) {
        PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(soundEvent, pitch, volume));
    }

    static void soundForUI(ServerPlayer serverPlayer, SoundEvent soundEvent) {
        PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(soundEvent));
    }

    static void animatePlayer(ServerPlayer serverPlayer, String anim) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new AnimatePlayerSC2(anim, serverPlayer));
    }

    static void simpleEffectSync(LivingEntity entity, MobEffect mobEffect, boolean present) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new SimpleEffectSyncingSC2(entity, mobEffect, present));
    }
}
