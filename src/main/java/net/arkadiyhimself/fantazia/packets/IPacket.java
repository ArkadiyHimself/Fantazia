package net.arkadiyhimself.fantazia.packets;

import net.arkadiyhimself.fantazia.api.prompt.Prompt;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.screen.AmplifyResource;
import net.arkadiyhimself.fantazia.data.talent.Talent;
import net.arkadiyhimself.fantazia.entities.DashStone;
import net.arkadiyhimself.fantazia.packets.attachment_modify.*;
import net.arkadiyhimself.fantazia.packets.attachment_syncing.*;
import net.arkadiyhimself.fantazia.packets.commands.BuildAuraTooltipSC2;
import net.arkadiyhimself.fantazia.packets.commands.BuildRuneTooltipSC2;
import net.arkadiyhimself.fantazia.packets.commands.BuildSpellTooltipSC2;
import net.arkadiyhimself.fantazia.packets.stuff.*;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public interface IPacket extends CustomPacketPayload {

    void handle(IPayloadContext context);

    /**
     * Attachment modifying
     */

    static void allInPreviousOutcome(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new AllInPreviousOutcomeS2C(serverPlayer.getData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME)));
    }

    static void barrierChanged(LivingEntity entity, float health) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new BarrierChangedSC2(entity.getId(), health));
    }

    static void barrierDamaged(LivingEntity entity, float damage) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new BarrierDamagedS2C(entity.getId(), damage));
    }

    static void beginDash() {
        PacketDistributor.sendToServer(new BeginDashC2S());
    }

    static void blockAttack(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new BlockAttackSC2());
    }

    static void cancelDash() {
        PacketDistributor.sendToServer(new CancelDashS2C());
    }

    static void effectSync(LivingEntity entity, MobEffect mobEffect, boolean present) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new EffectSyncSC2(entity, mobEffect, present));
    }

    static void entityMadeSound(ServerPlayer serverPlayer, LivingEntity entity) {
        PacketDistributor.sendToPlayer(serverPlayer, new EntityMadeSoundS2C(entity.getId()));
    }

    static void increaseEuphoria(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new IncreaseEuphoriaSC2());
    }

    static void jumpButtonRelease() {
        PacketDistributor.sendToServer(new JumpButtonReleasedC2S());
    }

    static void layeredBarrierChanged(LivingEntity entity, int layers) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new LayeredBarrierChangedSC2(entity.getId(), layers));
    }

    static void layeredBarrierDamaged(LivingEntity entity) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new LayeredBarrierDamagedS2C(entity.getId()));
    }

    static void manaChanged(ServerPlayer serverPlayer, float value) {
        PacketDistributor.sendToPlayer(serverPlayer, new ManaChangedS2C(value));
    }

    static void parryAttack(ServerPlayer serverPlayer, float amount) {
        PacketDistributor.sendToPlayer(serverPlayer, new ParryAttackS2C(amount));
    }

    static void performDoubleJump(boolean flying) {
        PacketDistributor.sendToServer(new PerformDoubleJumpC2S(flying));
    }

    static void pogoPlayer(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new PogoPlayerSC2());
    }

    static void puppeteerChange(ServerPlayer serverPlayer, boolean value) {
        PacketDistributor.sendToPlayer(serverPlayer, new PuppeteerChangeSC2(value));
    }

    static void reflectActivate(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new ReflectActivateSC2(serverPlayer.getId()));
    }

    static void resetEuphoria(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new ResetEuphoriaSC2());
    }

    static void revokeAllTalents(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new RevokeAllTalentsS2C());
    }

    static void setDashStoneEntity(ServerPlayer serverPlayer, int id) {
        PacketDistributor.sendToPlayer(serverPlayer, new SetDashStoneEntitySC2(id));
    }

    static void setWisdom(ServerPlayer serverPlayer, int amount) {
        PacketDistributor.sendToPlayer(serverPlayer, new SetWisdomSC2(amount));
    }

    static void staminaChanged(ServerPlayer serverPlayer, float value, int delay) {
        PacketDistributor.sendToPlayer(serverPlayer, new StaminaChangedSC2(value, delay));
    }

    static void startBlocking() {
        PacketDistributor.sendToServer(new StartBlockingC2S());
    }

    static void stopDash(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new StopDashS2C());
    }

    static void successfulEvasion(LivingEntity livingEntity) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new SuccessfulEvasionSC2(livingEntity.getId()));
    }

    static void talentBuying(ResourceLocation location) {
        PacketDistributor.sendToServer(new TalentBuyingC2S(location));
    }

    static void talentDisable(ResourceLocation location) {
        PacketDistributor.sendToServer(new TalentDisableC2S(location));
    }

    static void talentPossession(ServerPlayer serverPlayer, Talent talent, boolean unlocked) {
        PacketDistributor.sendToPlayer(serverPlayer, new TalentPossessionSC2(talent.id(), unlocked));
    }

    static void tickingIntegerUpdate(Entity entity, ResourceLocation location, int value) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new TickingIntegerUpdateS2C(location, value, entity.getId()));
    }

    static void wanderersSpiritLocation(ServerPlayer serverPlayer, boolean sound) {
        PacketDistributor.sendToPlayer(serverPlayer, new WanderersSpiritLocationS2C(serverPlayer.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION).serialize(), sound));
    }

    static void wisdomObtained(ServerPlayer serverPlayer, int amount) {
        PacketDistributor.sendToPlayer(serverPlayer, new WisdomObtainedSC2(amount));
    }

    /**
     * Attachment syncing
     */

    static void simpleMobEffectSyncing(CompoundTag tag, LivingEntity livingEntity) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new SimpleMobEffectSyncingS2C(tag, livingEntity.getId()));
    }

    /**
     * Commands
     */

    static void buildAuraTooltip(ServerPlayer serverPlayer, ResourceLocation id) {
        PacketDistributor.sendToPlayer(serverPlayer, new BuildAuraTooltipSC2(id));
    }

    static void buildRuneTooltip(ServerPlayer serverPlayer, ResourceLocation id) {
        PacketDistributor.sendToPlayer(serverPlayer, new BuildRuneTooltipSC2(id));
    }

    static void buildSpellTooltip(ServerPlayer serverPlayer, ResourceLocation id) {
        PacketDistributor.sendToPlayer(serverPlayer, new BuildSpellTooltipSC2(id));
    }

    /**
     * Stuff
     */

    static void addChasingParticles(Entity entity, List<ParticleOptions> options) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new AddChasingParticlesS2C(options));
    }

    static void addDashStoneProtectors(DashStone dashStone, List<Integer> ids) {
        PacketDistributor.sendToAllPlayers(new AddDashStoneProtectorsSC2(dashStone.getId(), ids));
    }

    static void addParticlesOnEntity(Entity entity, ParticleOptions particle, ParticleMovement movement, int amount, float range) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new AddParticlesOnEntitySC2(entity.getId(), particle, movement, amount, range));
    }

    static void amplificationMenuEnoughResources(ServerPlayer serverPlayer, AmplifyResource enoughWisdom, AmplifyResource enoughSubstance) {
        PacketDistributor.sendToPlayer(serverPlayer, new AmplificationMenuEnoughResourcesSC2(enoughWisdom, enoughSubstance));
    }

    static void animatePlayer(ServerPlayer serverPlayer, String anim) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new AnimatePlayerSC2(anim, serverPlayer));
    }

    static void interruptPlayer(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new InterruptPlayerS2C());
    }

    static void keyInput(KeyInputC2S.INPUT input, int action) {
        PacketDistributor.sendToServer(new KeyInputC2S(input, action));
    }

    static void playSoundForUI(ServerPlayer serverPlayer, SoundEvent soundEvent) {
        PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(soundEvent));
    }

    static void promptPlayer(ServerPlayer serverPlayer, Prompt prompt) {
        PacketDistributor.sendToPlayer(serverPlayer, new PromptPlayerSC2(prompt));
    }

    static void swingHand(ServerPlayer serverPlayer, InteractionHand hand) {
        PacketDistributor.sendToPlayer(serverPlayer, new SwingHandS2C(hand));
    }

    static void usedPrompt(Prompt prompt) {
        PacketDistributor.sendToServer(new UsedPromptC2S(prompt));
    }
}
