package net.arkadiyhimself.fantazia.packets.attachment_modify;

import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.TickingIntegerHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.EuphoriaHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.VibrationListenerHolder;
import net.arkadiyhimself.fantazia.events.ClientEvents;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

interface AttachmentModifyHandlers {

     static void allInPreviousOutcome(int value) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player == null) return;
         player.setData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME, value);
    }

    static void entityMadeSound(int id) {
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (!(entity instanceof LivingEntity livingEntity) || localPlayer == null) return;
        PlayerAbilityHelper.acceptConsumer(localPlayer, VibrationListenerHolder.class, vibrationListen -> vibrationListen.madeSound(livingEntity));
    }

    static void reflectLayerActivate() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        player.getData(FTZAttachmentTypes.REFLECT_RENDER_VALUES).reflect();
    }

    static void resetEuphoria() {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) PlayerAbilityHelper.acceptConsumer(player, EuphoriaHolder.class, EuphoriaHolder::reset);
    }

    static void simpleEffectSyncing(int id, ResourceLocation location, boolean present) {
         ClientLevel clientLevel = Minecraft.getInstance().level;
         MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(location);
         if (clientLevel == null || mobEffect == null) return;
         Entity entity = clientLevel.getEntity(id);
         if (entity instanceof LivingEntity livingEntity) LivingEffectHelper.simpleSetEffect(livingEntity, mobEffect, present);
    }

    static void soundExpired(int id) {
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (!(entity instanceof LivingEntity livingEntity) || localPlayer == null) return;
        PlayerAbilityHelper.acceptConsumer(localPlayer, VibrationListenerHolder.class, vibrationListen -> vibrationListen.soundExpired(livingEntity));
    }

    static void tickingIntegerUpdate(ResourceLocation resourceKey, int value, int entityId) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(entityId);
        AttachmentType<?> attachmentType = NeoForgeRegistries.ATTACHMENT_TYPES.get(resourceKey);
        if (entity == null || attachmentType == null) return;
        Object att = entity.getData(attachmentType);
        if (att instanceof TickingIntegerHolder attachment) attachment.set(value);
    }

    static void wanderersSpiritLocation(Vector3f vec3) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        player.setData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION, vec3.length() == 0 ? Vec3.ZERO : new Vec3(vec3));
        if (vec3.length() != 0) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(FTZSoundEvents.WANDERERS_SPIRIT_MARKED,1f));
    }

    static void wisdomObtained(int amount) {
        ClientEvents.lastWisdom = amount;
        if (amount > 0) ClientEvents.wisdomTick = 60;
    }
}
