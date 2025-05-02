package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.ITalentListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.custom_events.BlockingEvent;
import net.arkadiyhimself.fantazia.data.criterion.MeleeBlockTrigger;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.arkadiyhimself.fantazia.entities.magic_projectile.AbstractMagicProjectile;
import net.arkadiyhimself.fantazia.events.FantazicHooks;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.packets.stuff.SwingHandS2C;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class MeleeBlockHolder extends PlayerAbilityHolder implements ITalentListener, IDamageEventListener {

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
        super(player, Fantazia.res("melee_block"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean("unlocked", this.unlocked);
        tag.putBoolean("bloodloss", this.bloodloss);
        tag.putBoolean("disarm", this.disarm);

        if (anim == BLOCK_ANIM - 1) tag.putInt("anim", this.anim);
        tag.putInt("blockCooldown", this.blockCooldown);
        tag.putInt("blockTicks", this.blockTicks);
        tag.putInt("parryTicks", this.parryTicks);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.unlocked = compoundTag.getBoolean("unlocked");
        this.bloodloss = compoundTag.getBoolean("bloodloss");
        this.disarm = compoundTag.getBoolean("disarm");

        if (compoundTag.contains("anim")) this.anim = compoundTag.getInt("anim");
        this.blockCooldown = compoundTag.getInt("blockCooldown");
        this.blockTicks = compoundTag.getInt("blockTicks");
        this.parryTicks = compoundTag.getInt("parryTicks");
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

        if (parryDelay == 2 && parried && getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new SwingHandS2C(InteractionHand.MAIN_HAND));
        if (parried && parryDelay == 0) {
            boolean flag2 = CommonHooks.fireSweepAttack(getPlayer(), lastAttacker,false).isSweeping();
            if (flag2) getPlayer().sweepAttack();

            Vec3 horLook = getPlayer().getLookAngle().subtract(0, getPlayer().getLookAngle().y(), 0);
            AABB aabb = getPlayer().getBoundingBox().move(horLook.normalize().scale(2f)).inflate(1.5,0.25,1.5);

            DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(getPlayer().level());

            if (sources != null) {
                for (LivingEntity livingentity : getPlayer().level().getEntitiesOfClass(LivingEntity.class, aabb, livingEntity -> livingEntity.isAlive() && livingEntity != getPlayer() && livingEntity != lastAttacker)) {
                    float damageBonus = EnchantmentHelper.modifyDamage(serverLevel, getPlayer().getMainHandItem(), livingentity, sources.parry(getPlayer()), dmgParry);
                    boolean damaged = livingentity.hurt(sources.parry(getPlayer()), damageBonus);
                    if (bloodloss && damaged) LivingEffectHelper.giveHaemorrhage(livingentity, 200);

                }
                AttributeInstance instance = getPlayer().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
                double reach = instance == null ? 3 : instance.getValue();
                if (getPlayer().distanceTo(lastAttacker) <= reach) {
                    float damageBonus = EnchantmentHelper.modifyDamage(serverLevel, getPlayer().getMainHandItem(), lastAttacker, sources.parry(getPlayer()), dmgParry);
                    boolean damaged = lastAttacker.hurt(sources.parry(getPlayer()), damageBonus);
                    if (bloodloss && damaged) LivingEffectHelper.giveHaemorrhage(lastAttacker, 200);
                }
                if (Fantazia.DEVELOPER_MODE) getPlayer().sendSystemMessage(Component.translatable(String.valueOf(dmgParry)));
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
        Entity attacker = event.getSource().getDirectEntity();
        Vec3 sourcePos = event.getSource().getSourcePosition();

        boolean cancel = true;
        if (sourcePos == null || !PlayerAbilityHelper.facesAttack(getPlayer(), sourcePos)) return;

        if ((event.getSource().is(DamageTypes.MOB_ATTACK) || event.getSource().is(DamageTypes.PLAYER_ATTACK)) && attacker instanceof LivingEntity livingAtt) {

            if (blockTicks > 0) {
                lastAttacker = livingAtt;
                dmgTaken = event.getAmount();

                BlockingEvent.ParryDecision decision = FantazicHooks.onParryDecision(getPlayer(), getPlayer().getMainHandItem(), event.getAmount(), livingAtt);
                if (decision.getResult() == BlockingEvent.ParryDecision.Result.DO_PARRY || (parryTicks > 0 && decision.getResult() != BlockingEvent.ParryDecision.Result.DO_NOT_PARRY)) {
                    // parrying
                    AttributeInstance attackDamage = getPlayer().getAttribute(Attributes.ATTACK_DAMAGE);
                    float DMG = attackDamage == null ? 8f : (float) attackDamage.getValue() * 2;
                    if (parryAttack(DMG)) getPlayer().getMainHandItem().hurtAndBreak(1, livingAtt, EquipmentSlot.MAINHAND);
                    else cancel = false;
                    // regular blocking
                } else if (blockAttack()) getPlayer().getMainHandItem().hurtAndBreak(2, livingAtt, EquipmentSlot.MAINHAND);
            } else if (blockedTime <= 0 && parryDelay <= 0) cancel = false;

        } else if (attacker instanceof AbstractMagicProjectile projectileAtt && blockTicks > 0) {
            projectileAtt.deflect(getPlayer());
            cancel = projectileAtt.isMeleeBlocked() && blockAttack();
        }

        event.setCanceled(cancel);
    }

    @Override
    public void onTalentUnlock(ITalent talent) {
        ResourceLocation location = talent.getID();

        if (Fantazia.res("melee_block/melee_block").equals(location)) unlocked = true;
        if (Fantazia.res("melee_block/parry_haemorrhage").equals(location)) bloodloss = true;
        if (Fantazia.res("melee_block/parry_disarm").equals(location)) disarm = true;
    }

    @Override
    public void onTalentRevoke(ITalent talent) {
        ResourceLocation location = talent.getID();

        if (Fantazia.res("melee_block/melee_block").equals(location)) unlocked = false;
        if (Fantazia.res("melee_block/parry_haemorrhage").equals(location)) bloodloss = false;
        if (Fantazia.res("melee_block/parry_disarm").equals(location)) disarm = false;
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

        if (lastAttacker != null && disarm) LivingEffectHelper.makeDisarmed(lastAttacker, 160);


        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.COMBAT_MELEE_BLOCK.get(), SoundSource.PLAYERS);
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.animatePlayer(serverPlayer, "parry");;

        triggerMeleeBlock(true);

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

        if (lastAttacker != null) LivingEffectHelper.microStun(lastAttacker);
        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.COMBAT_MELEE_BLOCK.get(), SoundSource.PLAYERS);

        triggerMeleeBlock(false);

        return true;
    }

    public void startBlocking() {
        if (!unlocked || blockCooldown > 0 || !FantazicHooks.onBlockingStart(getPlayer(), getPlayer().getMainHandItem())) return;
        blockCooldown = BLOCK_CD;
        blockTicks = BLOCK_WINDOW;
        parryTicks = getPlayer().level() instanceof ServerLevel serverLevel ? getParryWindow(serverLevel) : PARRY_WINDOW;
        anim = BLOCK_ANIM;
        expiring = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.animatePlayer(serverPlayer, "blocking");;
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

        int blocks = customCriteriaHolder.performAction(Fantazia.res("blocked_attack"), 1);
        int parries = customCriteriaHolder.performAction(Fantazia.res("parried_attack"), parried ? 1 : 0);

        MeleeBlockTrigger.INSTANCE.trigger(serverPlayer, blocks, parries, parried, lastAttacker);
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
}
