package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITalentRequire;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.api.fantazicevents.BlockingEvent;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;

public class MeleeBlock extends AbilityHolder implements ITicking, ITalentRequire, IDamageReacting {
    private static final String ID = "melee_block:";
    private final DamageSource PARRY;
    private static final int BLOCK_ANIM = 20;
    private static final int BLOCK_WINDOW = 13;
    private static final int PARRY_WINDOW = 3;
    private static final int PARRY_DELAY = 8;
    private static final int BLOCK_TIME = 12;
    private static final int BLOCK_CD = 25;
    private LivingEntity attacker = null;
    private int anim;
    private int block_ticks;
    private int parry_ticks;
    private int parry_delay;
    private int blockedTime;
    private int block_cd;
    private boolean parried = false;
    private boolean expiring = false;
    private float dmgTaken;
    private float dmgParry;
    public MeleeBlock(Player player) {
        super(player);
        this.PARRY = new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FTZDamageTypes.PARRY), player);
    }
    @Override
    public void tick() {
        if (!(getPlayer() instanceof ServerPlayer serverPlayer)) return;
        anim = Math.max(0, anim - 1);
        block_ticks = Math.max(0, block_ticks - 1);
        parry_ticks = Math.max(0, parry_ticks - 1);
        parry_delay = Math.max(0, parry_delay - 1);
        blockedTime = Math.max(0, blockedTime - 1);
        block_cd = Math.max(0, block_cd - 1);
        if (parried && parry_delay == 0) {
            Vec3 horLook = serverPlayer.getLookAngle().subtract(0, serverPlayer.getLookAngle().y(), 0);
            AABB aabb = serverPlayer.getBoundingBox().move(horLook.normalize().scale(2f)).inflate(1.5,0.25,1.5);

            for(LivingEntity livingentity : serverPlayer.level().getEntitiesOfClass(LivingEntity.class, aabb, livingEntity -> livingEntity.isAlive() && livingEntity != serverPlayer && livingEntity != attacker)) livingentity.hurt(PARRY, dmgParry);
            AttributeInstance instance = getPlayer().getAttribute(ForgeMod.ENTITY_REACH.get());
            double reach = instance == null ? 3 : instance.getValue();
            if (getPlayer().distanceTo(attacker) <= reach) attacker.hurt(PARRY, dmgParry);

            parried = false;
            block_cd = 0;
        }
        if (expiring && block_ticks == 0) {
            FTZEvents.onBlockingExpired(serverPlayer, serverPlayer.getMainHandItem());
            expiring = false;
        }
    }
    @Override
    public void respawn() {
        anim = 0;
        block_ticks = 0;
        parry_ticks = 0;
        parry_delay = 0;
        blockedTime = 0;
        block_cd = 0;
        parried = false;
        expiring = false;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(ID + "block_cd", this.block_cd);
        tag.putInt(ID + "block_ticks", this.block_ticks);
        tag.putInt(ID + "parry_ticks", this.parry_ticks);
        tag.putInt(ID + "anim", this.anim);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        this.block_cd = tag.contains(ID + "block_cd") ? tag.getInt(ID +"block_cd") : 0;
        this.block_ticks = tag.contains(ID + "block_ticks") ? tag.getInt(ID +"block_ticks") : 0;
        this.parry_ticks = tag.contains(ID + "parry_ticks") ? tag.getInt(ID + "parry_ticks") : 0;
        this.anim = tag.contains(ID + "anim") ? tag.getInt(ID + "anim") : 0;
    }
    @Override
    public void onTalentUnlock(BasicTalent talent) {
    }
    @Override
    public void onTalentRevoke(BasicTalent talent) {
    }
    @Override
    public void onHit(LivingAttackEvent event) {
        if ((event.getSource().is(DamageTypes.MOB_ATTACK) || event.getSource().is(DamageTypes.PLAYER_ATTACK)) && event.getSource().getEntity() instanceof LivingEntity atk && getPlayer() instanceof ServerPlayer serverPlayer) {
            boolean blocked = AbilityHelper.facesAttack(serverPlayer, event.getSource());
            if (blocked && block_ticks > 0) {
                BlockingEvent.ParryDecision decision = FTZEvents.onParryDecision(serverPlayer, serverPlayer.getMainHandItem(), event.getAmount(), atk);
                if (decision.getResult() == Event.Result.ALLOW) {
                    boolean parry = parryAttack((float) (serverPlayer.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2), serverPlayer);
                    if (parry) {
                        serverPlayer.getMainHandItem().hurtAndBreak(1, atk, (livingEntity1 -> livingEntity1.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
                        event.setCanceled(true);
                    }
                } else if (decision.getResult() == Event.Result.DENY) {
                    boolean block = blockAttack();
                    if (block) {
                        serverPlayer.getMainHandItem().hurtAndBreak(2, atk, (livingEntity1 -> livingEntity1.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
                        event.setCanceled(true);
                    }
                } else {
                    dmgTaken = event.getAmount();
                    attacker = atk;
                    if (parry_ticks > 0) {
                        boolean parry = parryAttack((float) (serverPlayer.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2), serverPlayer);
                        if (parry) {
                            serverPlayer.getMainHandItem().hurtAndBreak(1, atk, (livingEntity1 -> livingEntity1.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
                            event.setCanceled(true);
                        }
                    } else {
                        boolean block = blockAttack();
                        if (block) {
                            serverPlayer.getMainHandItem().hurtAndBreak(2, atk, (livingEntity1 -> livingEntity1.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
                            event.setCanceled(true);
                        }
                    }
                }
            }
            if (parry_delay > 0 || blockedTime > 0) {
                event.setCanceled(true);
            }
        }
    }
    public boolean isInAnim() {
        return (anim > 0 || parry_delay > 0 || blockedTime > 0);
    }
    public boolean parryAttack(float amount, ServerPlayer serverPlayer) {
        BlockingEvent.Parry parryEvent = FTZEvents.onParry(serverPlayer, serverPlayer.getMainHandItem(), dmgTaken, attacker, amount);
        if (!parryEvent.isCanceled()) {
            dmgParry = parryEvent.getParryDamage();
            parry_delay = PARRY_DELAY;
            parried = true;
            block_cd = 0;
            block_ticks = 0;
            anim = 0;
            expiring = false;
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), FTZSoundEvents.BLOCKED, SoundSource.PLAYERS);
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("parry"), serverPlayer);
        }
        return !parryEvent.isCanceled();
    }
    public boolean blockAttack() {
        if (!(getPlayer() instanceof ServerPlayer serverPlayer)) return false;
        boolean blocked = FTZEvents.onBlock(serverPlayer, serverPlayer.getMainHandItem(), dmgTaken, attacker);
        if (blocked) {
            serverPlayer.getMainHandItem().hurtAndBreak(2, serverPlayer, (durability) -> durability.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("block"), serverPlayer);
            blockedTime = BLOCK_TIME;
            block_ticks = 0;
            anim = 0;
            expiring = false;
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), FTZSoundEvents.BLOCKED, SoundSource.PLAYERS);
        }
        return blocked;
    }
    public void startBlocking() {
        if (block_cd == 0 && getPlayer() instanceof ServerPlayer serverPlayer) {
            boolean startBlock = FTZEvents.onBlockingStart(serverPlayer, serverPlayer.getMainHandItem());
            if (startBlock) {
                block_cd = BLOCK_CD;
                block_ticks = BLOCK_WINDOW;
                parry_ticks = PARRY_WINDOW;
                anim = BLOCK_ANIM;
                expiring = true;
                NetworkHandler.sendToPlayer(new PlayAnimationS2C("blocking"), serverPlayer);
            }
        }
    }
    public void interrupt() {
        anim = 0;
        block_ticks = 0;
        parry_ticks = 0;
        parry_delay = 0;
        blockedTime = 0;
        parried = false;
        expiring = false;
    }
}
