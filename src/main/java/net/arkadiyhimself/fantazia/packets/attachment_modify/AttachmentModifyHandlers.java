package net.arkadiyhimself.fantazia.packets.attachment_modify;

import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.LocationHolder;
import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.TickingIntegerHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.PuppeteeredEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.events.ClientEvents;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

interface AttachmentModifyHandlers {

     static void allInPreviousOutcome(int value) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player == null) return;
         player.setData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME, value);
    }

    static void barrierAdded(int id, float health) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        Entity entity;
        if (clientLevel == null || (entity = clientLevel.getEntity(id)) == null) return;
        entity.setData(FTZAttachmentTypes.BARRIER_HEALTH, health);
    }

    static void barrierDamaged(int id, float damage) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        Entity entity;
        if (clientLevel == null || (entity = clientLevel.getEntity(id)) == null) return;
        float health = entity.getData(FTZAttachmentTypes.BARRIER_HEALTH);
        entity.setData(FTZAttachmentTypes.BARRIER_HEALTH, Math.max(0, health - damage));
        entity.setData(FTZAttachmentTypes.BARRIER_COLOR, 1f);
    }

    static void beginDash(ServerPlayer player) {
         PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::beginDash);
    }

    static void blockAttack() {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, MeleeBlockHolder.class, MeleeBlockHolder::blockAttack);
    }

    static void cancelDash(ServerPlayer serverPlayer) {
         PlayerAbilityHelper.acceptConsumer(serverPlayer, DashHolder.class, DashHolder::maybeCancelDash);
    }

    static void entityMadeSound(int id) {
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(id);
        if (!(entity instanceof LivingEntity livingEntity)) return;
        PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, VibrationListenerHolder.class, vibrationListen -> vibrationListen.madeSound(livingEntity));
    }

    static void increaseEuphoria() {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, EuphoriaHolder.class, EuphoriaHolder::increase);
    }

    static void jumpButtonReleased(ServerPlayer serverPlayer) {
         PlayerAbilityHelper.acceptConsumer(serverPlayer, DoubleJumpHolder.class, DoubleJumpHolder::buttonRelease);
    }

    static void layeredBarrierAdded(int id, int layers) {
         ClientLevel clientLevel = Minecraft.getInstance().level;
         Entity entity;
         if (clientLevel == null || (entity = clientLevel.getEntity(id)) == null) return;
         entity.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, layers);
    }

    static void layeredBarrierDamaged(int id) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        Entity entity;
        if (clientLevel == null || (entity = clientLevel.getEntity(id)) == null) return;
        int layers = entity.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS);
        entity.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, --layers);
        entity.setData(FTZAttachmentTypes.LAYERED_BARRIER_COLOR, 1f);
    }

    static void manaChanged(float value) {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, ManaHolder.class, manaHolder -> manaHolder.setMana(value));
    }

    static void parryAttack(float amount) {
        PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, MeleeBlockHolder.class, holder -> holder.parryAttack(amount));
    }

    static void pogoPlayer() {
         if (Minecraft.getInstance().player != null) PlayerAbilityHelper.pogo(Minecraft.getInstance().player);
    }

    static void performDoubleJump(ServerPlayer serverPlayer, boolean flying) {
        PlayerAbilityHelper.acceptConsumer(serverPlayer, DoubleJumpHolder.class, doubleJumpHolder -> doubleJumpHolder.successfulJump(flying));
    }

    static void puppeteerChange(boolean value) {
         LivingEffectHelper.acceptConsumer(Minecraft.getInstance().player, PuppeteeredEffectHolder.class, holder -> holder.setPuppetBoolean(value));
     }

    static void reflectLayerActivate(int id) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity entity = level.getEntity(id);
        if (entity != null) entity.getData(FTZAttachmentTypes.REFLECT_RENDER_VALUES).reflect();
    }

    static void resetEuphoria() {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, EuphoriaHolder.class, EuphoriaHolder::reset);
    }

    static void revokeAllTalents() {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, TalentsHolder.class, TalentsHolder::revokeAll);
    }

    static void setDashStoneEntity(int id) {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, DashHolder.class, dashHolder -> dashHolder.setDashstoneEntityClient(id));
    }

    static void setWisdom(int amount) {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, TalentsHolder.class, talentsHolder -> talentsHolder.setWisdom(amount));
    }

    static void simpleEffectSyncing(int id, ResourceLocation location, boolean present) {
         ClientLevel clientLevel = Minecraft.getInstance().level;
         MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(location);
         if (clientLevel == null || mobEffect == null) return;
         Entity entity = clientLevel.getEntity(id);
         if (entity instanceof LivingEntity livingEntity) LivingEffectHelper.simpleSetEffect(livingEntity, mobEffect, present);
    }

    static void staminaChanged(float value, int delay) {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, StaminaHolder.class, staminaHolder -> staminaHolder.setStamina(value, delay));
    }

    static void startBlocking(ServerPlayer serverPlayer) {
         PlayerAbilityHelper.acceptConsumer(serverPlayer, MeleeBlockHolder.class, MeleeBlockHolder::startBlocking);
    }

    static void stopDash() {
         PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, DashHolder.class, DashHolder::stopDash);
    }

    static void successfulEvasion(int id) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(id) instanceof LivingEntity livingEntity)) return;

        LivingDataHelper.acceptConsumer(livingEntity, EvasionHolder.class, EvasionHolder::success);
    }

    static void talentBuying(ServerPlayer player, ResourceLocation location) {
        Talent talent = ServerTalentManager.getTalent(location);
        if (talent != null) PlayerAbilityHelper.acceptConsumer(player, TalentsHolder.class, talentsHolder -> talentsHolder.tryBuyTalent(talent));
    }

    static void talendDisable(ServerPlayer player, ResourceLocation location) {
         Talent talent = ServerTalentManager.getTalent(location);
         if (talent != null) PlayerAbilityHelper.acceptConsumer(player, TalentsHolder.class, holder -> holder.clickedTalent(talent));
    }

    static void talentPossession(ResourceLocation location, boolean unlocked) {
         Talent talent = ServerTalentManager.getTalent(location);
         TalentsHolder holder = PlayerAbilityHelper.takeHolder(Minecraft.getInstance().player, TalentsHolder.class);
         if (talent == null || holder == null) return;
         if (unlocked) holder.tryObtainTalent(talent);
         else holder.tryRevokeTalent(talent);
    }

    static void tickingIntegerUpdate(ResourceLocation location, int value, int entityId) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(entityId);
        AttachmentType<?> attachmentType = NeoForgeRegistries.ATTACHMENT_TYPES.get(location);
        if (entity == null || attachmentType == null) return;
        Object att = entity.getData(attachmentType);
        if (att instanceof TickingIntegerHolder attachment) attachment.set(value);
    }

    static void wanderersSpiritLocation(CompoundTag tag, boolean sound) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        LocationHolder holder = player.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);
        holder.deserialize(tag);
        if (!holder.empty() && sound) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(FTZSoundEvents.WANDERERS_SPIRIT_MARKED,1f));
    }

    static void wisdomObtained(int amount) {
        ClientEvents.lastWisdom = amount;
        if (amount > 0) ClientEvents.wisdomTick = 60;
        PlayerAbilityHelper.acceptConsumer(Minecraft.getInstance().player, TalentsHolder.class, talentsHolder -> talentsHolder.grantWisdom(amount));
    }
}
