package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.BlockingEvent;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.NewEvents;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.combatimprovement.api.DamageTypeRegistry;
import net.arkadiyhimself.combatimprovement.api.SoundRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class Blocking extends PlayerCapability {
    public final DamageSource PARRY = new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.PARRY), player);
    public Blocking(Player player) { super(player); }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.player.getId(), AttachBlocking.BLOCKING_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("blockCooldown", this.blockCooldown);
        tag.putInt("block", this.block);
        tag.putInt("parry", this.parry);
        tag.putInt("blockAnim", this.blockAnim);
        tag.putBoolean("postEndEvent", this.postEndEvent);
        tag.putInt("parryDMGdelay", this.parryDMGdelay);
        tag.putInt("blockedTime", this.blockedTime);
        tag.putBoolean("justParried", this.justParried);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.blockCooldown = nbt.contains("blockCooldown") ? nbt.getInt("blockCooldown") : 0;
        this.block = nbt.contains("block") ? nbt.getInt("block") : 0;
        this.parry = nbt.contains("parry") ? nbt.getInt("parry") : 0;
        this.blockAnim = nbt.contains("blockAnim") ? nbt.getInt("blockAnim") : 0;
        this.postEndEvent = nbt.contains("postEndEvent") && nbt.getBoolean("postEndEvent");
        this.parryDMGdelay = nbt.contains("parryDMGdelay") ? nbt.getInt("parryDMGdelay") : 0;
        this.blockedTime = nbt.contains("blockedTime") ? nbt.getInt("blockedTime") : 0;
        this.justParried = nbt.contains("justParried") && nbt.getBoolean("justParried");
    }

    // blocking attacks stuff
    public LivingEntity attacker;
    public final int BLOCK_ANIM = 20;
    public final int BLOCK_WINDOW = 13;
    public final int PARRY_WINDOW = 3;
    public final int PARRY_DMG_DELAY = 8;
    public final int BLOCKED_TIME = 12;
    public final int BLOCK_COOLDOWN = 25;
    public int blockAnim;
    public int block;
    public int parry;
    public int parryDMGdelay;
    public int blockedTime;
    public int blockCooldown;
    public boolean justParried = false;
    public boolean postEndEvent = false;
    private float dmgTaken;
    private float dmgParry;
    public void ticking(ServerPlayer serverPlayer) {
        blockAnim = Math.max(0, blockAnim - 1);
        block = Math.max(0, block - 1);
        parry = Math.max(0, parry - 1);
        parryDMGdelay = Math.max(0, parryDMGdelay - 1);
        blockedTime = Math.max(0, blockedTime - 1);
        blockCooldown = Math.max(0, blockCooldown - 1);
        if (justParried && parryDMGdelay == 0) {
            if (attacker != null && serverPlayer.position().distanceTo(attacker.position()) <= serverPlayer.getAttribute(ForgeMod.ENTITY_REACH.get()).getValue()) {
                attacker.hurt(PARRY, dmgParry);
            }
            justParried = false;
            blockCooldown = 0;
        }
        if (postEndEvent && block == 0) {
            NewEvents.onBlockingEnd(serverPlayer, serverPlayer.getMainHandItem());
            postEndEvent = false;
        }
        updateTracking();
    }
    public boolean isInAnim() {
        return (blockAnim > 0 || parryDMGdelay > 0 || blockedTime > 0);
    }
    public boolean parryAttack(float amount, ServerPlayer serverPlayer) {
        BlockingEvent.Parry parried = NewEvents.onParry(serverPlayer, serverPlayer.getMainHandItem(), dmgTaken, attacker, amount);
        if (!parried.isCanceled()) {
            dmgParry = parried.getParryDamage();
            parryDMGdelay = PARRY_DMG_DELAY;
            justParried = true;
            blockCooldown = 0;
            block = 0;
            blockAnim = 0;
            postEndEvent = false;
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundRegistry.BLOCKED.get(), SoundSource.PLAYERS);
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("parry"), serverPlayer);
        }
        return !parried.isCanceled();
    }
    public boolean blockAttack(ServerPlayer serverPlayer) {
        boolean blocked = NewEvents.onBlock(serverPlayer, serverPlayer.getMainHandItem(), dmgTaken, attacker);
        if (blocked) {
            serverPlayer.getMainHandItem().hurtAndBreak(2, serverPlayer, (durability) -> durability.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("block"), serverPlayer);
            blockedTime = BLOCKED_TIME;
            block = 0;
            blockAnim = 0;
            postEndEvent = false;
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundRegistry.BLOCKED.get(), SoundSource.PLAYERS);
        }
        return blocked;
    }
    public void startBlocking(ServerPlayer serverPlayer) {
        if (blockCooldown == 0) {
            boolean startBlock = NewEvents.onBlockingStart(serverPlayer, serverPlayer.getMainHandItem());
            if (startBlock) {
                blockCooldown = BLOCK_COOLDOWN;
                block = BLOCK_WINDOW;
                parry = PARRY_WINDOW;
                blockAnim = BLOCK_ANIM;
                postEndEvent = true;
                NetworkHandler.sendToPlayer(new PlayAnimationS2C("blocking"), serverPlayer);
                updateTracking();
            }
        }
    }
    public void onHit(LivingAttackEvent event) {
        if (("mob".equals(event.getSource().getMsgId()) || "player".equals(event.getSource().getMsgId()))
                && event.getEntity() instanceof ServerPlayer serverPlayer) {
            boolean blocked = WhereMagicHappens.Abilities.blockedAttack(serverPlayer, event.getSource());
            if (blocked && block > 0) {
                dmgTaken = event.getAmount();
                attacker = (LivingEntity) event.getSource().getEntity();
                int i = 0;
                if (parry > 0) {
                    boolean parry = parryAttack((float) (serverPlayer.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2), serverPlayer);
                    event.setCanceled(parry);
                } else {
                    boolean block = blockAttack(serverPlayer);
                    event.setCanceled(block);
                }
            }
            if (parryDMGdelay > 0 || blockedTime > 0) {
                event.setCanceled(true);
            }
            updateTracking();
        }
    }
}
