package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.rune.RuneHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.custom_events.BlockingEvent;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.common.enchantment.effects.ParryModify;
import net.arkadiyhimself.fantazia.data.criterion.MeleeBlockTrigger;
import net.arkadiyhimself.fantazia.common.entity.magic_projectile.AbstractMagicProjectile;
import net.arkadiyhimself.fantazia.common.FantazicHooks;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class MeleeBlockHolder extends PlayerAbilityHolder implements IDamageEventListener {

    public static final int BLOCK_ANIM = 20;
    private static final int BLOCK_WINDOW = 13;
    private static final int PARRY_WINDOW = 4;
    private static final int PARRY_DELAY = 8;
    private static final int BLOCK_TIME = 12;
    private static final int BLOCK_CD = 25;

    private LivingEntity lastAttacker = null;

    private boolean unlocked = false;
    private boolean bloodloss = false;
    private boolean disarm = false;

    private int anim;
    private int blockTicks;
    private int parryTicks;
    private int parryDelay;
    private int blockedTime;
    private int blockCooldown;
    private boolean parried = false;
    private boolean expiring = false;
    private float dmgTaken;
    private float dmgParry;

    public MeleeBlockHolder(Player player) {
        super(player, Fantazia.location("melee_block"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("unlocked", this.unlocked);
        tag.putBoolean("bloodloss", this.bloodloss);
        tag.putBoolean("disarm", this.disarm);

        tag.putInt("anim", this.anim);
        tag.putInt("blockTicks", this.blockTicks);
        tag.putInt("parryTicks", this.parryTicks);
        tag.putInt("parryDelay", this.parryDelay);
        tag.putInt("blockedTime", this.blockedTime);
        tag.putInt("blockCooldown", this.blockCooldown);
        tag.putBoolean("parried", this.parried);
        tag.putBoolean("expiring", this.expiring);
        tag.putFloat("dmgTaken", this.dmgTaken);
        tag.putFloat("dmgParry", this.dmgParry);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.unlocked = tag.getBoolean("unlocked");
        this.bloodloss = tag.getBoolean("bloodloss");
        this.disarm = tag.getBoolean("disarm");

        this.anim = tag.getInt("anim");
        this.blockTicks = tag.getInt("blockTicks");
        this.parryTicks = tag.getInt("parryTicks");
        this.parryDelay = tag.getInt("parryDelay");
        this.blockedTime = tag.getInt("blockedTime");
        this.blockCooldown = tag.getInt("blockCooldown");
        this.parried = tag.getBoolean("parried");
        this.expiring = tag.getBoolean("expiring");
        this.dmgTaken = tag.getFloat("dmgTaken");
        this.dmgParry = tag.getFloat("dmgParry");
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("unlocked", this.unlocked);
        tag.putBoolean("bloodloss", this.bloodloss);
        tag.putBoolean("disarm", this.disarm);

        tag.putInt("anim", this.anim);
        tag.putInt("blockCooldown", this.blockCooldown);
        tag.putInt("blockTicks", this.blockTicks);
        tag.putInt("parryTicks", this.parryTicks);

        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        this.unlocked = tag.getBoolean("unlocked");
        this.bloodloss = tag.getBoolean("bloodloss");
        this.disarm = tag.getBoolean("disarm");

        this.anim = tag.getInt("anim");
        this.blockCooldown = tag.getInt("blockCooldown");
        this.blockTicks = tag.getInt("blockTicks");
        this.parryTicks = tag.getInt("parryTicks");
    }

    @Override
    public void respawn() {
        anim = 0;
        blockTicks = 0;
        parryTicks = 0;
        parryDelay = 0;
        blockedTime = 0;
        blockCooldown = 0;
        parried = false;
        expiring = false;
    }

    @Override
    public void serverTick() {
        if (!(getPlayer().level() instanceof ServerLevel serverLevel)) return;
        if (anim > 0) anim--;
        if (blockTicks > 0) blockTicks--;
        if (parryTicks > 0) parryTicks--;
        if (parryDelay > 0) parryDelay--;
        if (blockedTime > 0) blockedTime--;
        if (blockCooldown > 0) blockCooldown--;

        if (parryDelay == 2 && parried && getPlayer() instanceof ServerPlayer serverPlayer) IPacket.swingHand(serverPlayer, InteractionHand.MAIN_HAND);
        if (parried && parryDelay == 0) {
            if (meticulous()) ApplyEffect.giveAbsoluteBarrier(getPlayer(), 25);
            if (CommonHooks.fireSweepAttack(getPlayer(), lastAttacker,false).isSweeping()) getPlayer().sweepAttack();

            Vec3 horLook = getPlayer().getLookAngle().subtract(0, getPlayer().getLookAngle().y(), 0);
            AABB aabb = getPlayer().getBoundingBox().move(horLook.normalize().scale(2f)).inflate(1.5,0.25,1.5);

            DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(getPlayer().level());

            if (sources != null) {
                for (LivingEntity livingentity : getPlayer().level().getEntitiesOfClass(LivingEntity.class, aabb, livingEntity -> livingEntity.isAlive() && livingEntity != getPlayer() && livingEntity != lastAttacker)) {
                    float damageBonus = EnchantmentHelper.modifyDamage(serverLevel, getPlayer().getMainHandItem(), livingentity, sources.parry(getPlayer()), dmgParry);
                    boolean damaged = livingentity.hurt(sources.parry(getPlayer()), damageBonus);
                    if (bloodloss && damaged) ApplyEffect.giveHaemorrhage(livingentity, 200);
                }
                AttributeInstance instance = getPlayer().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
                double reach = instance == null ? 3 : instance.getValue();
                if (getPlayer().distanceTo(lastAttacker) <= reach) {
                    float damageBonus = EnchantmentHelper.modifyDamage(serverLevel, getPlayer().getMainHandItem(), lastAttacker, sources.parry(getPlayer()), dmgParry);
                    boolean damaged = lastAttacker.hurt(sources.parry(getPlayer()), damageBonus);
                    if (bloodloss && damaged) ApplyEffect.giveHaemorrhage(lastAttacker, 200);
                }
            }

            parried = false;
            blockCooldown = 0;
        }

        if (expiring && blockTicks == 0) {
            FantazicHooks.onBlockingExpired(getPlayer(), getPlayer().getMainHandItem());
            expiring = false;
        }
    }

    @Override
    public void clientTick() {
        if (anim > 0) anim--;
        if (blockTicks > 0) blockTicks--;
        if (parryTicks > 0) parryTicks--;
        if (parryDelay > 0) parryDelay--;
        if (blockedTime > 0) blockedTime--;
        if (blockCooldown > 0) blockCooldown--;
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getDirectEntity();
        Vec3 sourcePos = source.getSourcePosition();

        boolean cancel = false;
        if (sourcePos == null || !PlayerAbilityHelper.facesAttack(getPlayer(), sourcePos)) return;

        if ((source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK)) && attacker instanceof LivingEntity livingAtt) {
            if (blockTicks > 0) {
                lastAttacker = livingAtt;
                dmgTaken = event.getAmount();

                BlockingEvent.ParryDecision decision = FantazicHooks.onParryDecision(getPlayer(), getPlayer().getMainHandItem(), event.getAmount(), livingAtt);
                if (decision.getResult() == BlockingEvent.ParryDecision.Result.DO_PARRY || (parryTicks > 0 && decision.getResult() != BlockingEvent.ParryDecision.Result.DO_NOT_PARRY)) {
                    // parrying
                    AttributeInstance attackDamage = getPlayer().getAttribute(Attributes.ATTACK_DAMAGE);
                    float DMG = attackDamage == null ? 8f : (float) attackDamage.getValue() * getParryMultiplier(lastAttacker);
                    if (parryAttack(DMG)) {
                        cancel = true;
                        getPlayer().getMainHandItem().hurtAndBreak(1, livingAtt, EquipmentSlot.MAINHAND);
                    }
                    // regular blocking
                } else if (blockAttack()) {
                    cancel = true;
                    getPlayer().getMainHandItem().hurtAndBreak(2, livingAtt, EquipmentSlot.MAINHAND);
                }
            } else if (blockedTime > 0 || parryDelay > 0) cancel = true;

        } else if (attacker instanceof AbstractMagicProjectile projectileAtt && blockTicks > 0) {
            projectileAtt.deflect(getPlayer());
            cancel = projectileAtt.isMeleeBlocked() && blockAttack();
        }

        if (cancel) event.setCanceled(true);
    }

    public boolean isInAnim() {
        return (anim > 0 || parryDelay > 0 || blockedTime > 0);
    }

    public boolean parryAttack(float amount) {
        BlockingEvent.Parry parryEvent = FantazicHooks.onParry(getPlayer(), getPlayer().getMainHandItem(), dmgTaken, lastAttacker, amount);
        if (parryEvent.isCanceled()) return false;

        dmgParry = parryEvent.getParryDamage();
        parryDelay = PARRY_DELAY;
        blockedTime = BLOCK_TIME;
        parried = true;
        blockCooldown = 0;
        blockTicks = 0;
        anim = 0;
        expiring = false;

        if (lastAttacker != null && disarm) ApplyEffect.makeDisarmed(lastAttacker, 160);

        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.COMBAT_MELEE_BLOCK.get(), SoundSource.PLAYERS);
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.animatePlayer(serverPlayer, "parry");;

        triggerMeleeBlock(true);
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.parryAttack(serverPlayer, amount);

        return true;
    }

    public boolean blockAttack() {
        if (!FantazicHooks.onBlock(getPlayer(), getPlayer().getMainHandItem(), dmgTaken, lastAttacker)) return false;

        getPlayer().getMainHandItem().hurtAndBreak(2, getPlayer(), EquipmentSlot.MAINHAND);
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.animatePlayer(serverPlayer, "block");;

        blockedTime = BLOCK_TIME;
        blockTicks = 0;
        anim = 0;
        expiring = false;

        if (lastAttacker != null) ApplyEffect.microStun(lastAttacker);
        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.COMBAT_MELEE_BLOCK.get(), SoundSource.PLAYERS);

        triggerMeleeBlock(false);
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.blockAttack(serverPlayer);

        return true;
    }

    public void startBlocking() {
        if (getPlayer().hasEffect(FTZMobEffects.DISARM) || !unlocked || blockCooldown > 0 || !FantazicHooks.onBlockingStart(getPlayer(), getPlayer().getMainHandItem())) return;
        blockCooldown = BLOCK_CD;
        blockTicks = BLOCK_WINDOW;
        parryTicks = getPlayer().level() instanceof ServerLevel serverLevel ? getParryWindow(serverLevel) : PARRY_WINDOW;
        anim = BLOCK_ANIM;
        expiring = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            Prompts.USE_MELEE_BLOCK.noLongerNeeded(serverPlayer);
            IPacket.animatePlayer(serverPlayer, "blocking");
        }
        else IPacket.startBlocking();
    }

    public void interrupt() {
        anim = 0;
        blockTicks = 0;
        parryTicks = 0;
        parryDelay = 0;
        blockedTime = 0;
        parried = false;
        expiring = false;
    }

    public int getParryWindow(ServerLevel serverLevel) {
        return 6 - serverLevel.getDifficulty().getId();
    }

    private void triggerMeleeBlock(boolean parried) {
        if (!(getPlayer() instanceof ServerPlayer serverPlayer)) return;

        CustomCriteriaHolder customCriteriaHolder = PlayerAbilityHelper.takeHolder(serverPlayer, CustomCriteriaHolder.class);
        if (customCriteriaHolder == null) return;

        int blocks = customCriteriaHolder.performAction(Fantazia.location("blocked_attack"), 1);
        int parries = customCriteriaHolder.performAction(Fantazia.location("parried_attack"), parried ? 1 : 0);

        MeleeBlockTrigger.INSTANCE.trigger(serverPlayer, blocks, parries, parried, lastAttacker);
    }

    public void setUnlocked(boolean value) {
        this.unlocked = value;
    }

    public void setBloodloss(boolean value) {
        this.bloodloss = value;
    }

    public void setDisarm(boolean value) {
        this.disarm = value;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public int anim() {
        return anim;
    }

    public int blockCooldown() {
        return blockTicks;
    }

    public int blockTicks() {
        return blockTicks;
    }

    public int parryTicks() {
        return parryTicks;
    }

    private boolean meticulous() {
        return RuneHelper.hasRune(getPlayer(), Runes.METICULOUS);
    }

    private float getParryMultiplier(LivingEntity entity) {
        float basic = 2f;
        if (!(getPlayer().level() instanceof ServerLevel serverLevel)) return basic;
        DamageSourcesHolder damageSourcesHolder = LevelAttributesHelper.getDamageSources(serverLevel);
        if (damageSourcesHolder == null) return basic;
        ItemStack stack = getPlayer().getMainHandItem();

        DamageSource source = damageSourcesHolder.parry(getPlayer());

        float multiplier = ParryModify.modifyParry(serverLevel, stack, entity, source, basic);
        Fantazia.LOGGER.info("Parry multiplier: {}", multiplier);
        return multiplier;
    }
}
