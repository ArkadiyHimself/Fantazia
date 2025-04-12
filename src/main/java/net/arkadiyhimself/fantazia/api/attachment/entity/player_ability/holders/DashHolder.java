package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.ITalentListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.arkadiyhimself.fantazia.entities.DashStoneEntity;
import net.arkadiyhimself.fantazia.events.FantazicHooks;
import net.arkadiyhimself.fantazia.packets.stuff.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.packets.stuff.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class DashHolder extends PlayerAbilityHolder implements ITalentListener, IDamageEventListener {
    private static final float STAMINA = 1.5f;
    private static final int DEFAULT_DUR = 7;
    private static final int DEFAULT_RECHARGE = 100;
    private int dashstoneEntity = -1;
    private int initialDur = 1;
    private int initialRecharge = 1;
    private int duration = 0;
    private int recharge = 0;
    private int level = 0;
    private boolean wasDashing = false;
    private boolean midAir = false;
    private boolean recharged = false;
    private Vec3 velocity = new Vec3(0,0,0);

    public DashHolder(Player player) {
        super(player, Fantazia.res("dash"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", this.level);
        tag.putInt("initial_dur", this.initialDur);
        tag.putInt("duration", this.duration);
        tag.putInt("initial_recharge", this.initialRecharge);
        tag.putInt("recharge", this.recharge);
        if (dashstoneEntity != -1) tag.putInt("entity", dashstoneEntity);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.level = compoundTag.contains("level") ?  compoundTag.getInt("level") : 0;
        this.initialDur = compoundTag.contains("initial_dur") ?  compoundTag.getInt("initial_dur") : 1;
        this.duration = compoundTag.contains("duration") ?  compoundTag.getInt("duration") : 0;
        this.initialRecharge = compoundTag.contains("initial_recharge") ?  compoundTag.getInt("initial_recharge") : 1;
        this.recharge = compoundTag.contains("recharge") ?  compoundTag.getInt("recharge") : 0;
        this.dashstoneEntity = compoundTag.contains("entity") ? compoundTag.getInt("entity") : 0;
    }

    @Override
    public void respawn() {
        duration = 0;
        recharge = 0;
        wasDashing = false;
        recharged = false;
    }

    @Override
    public void tick() {
        if (isDashing()) {
            duration--;
            getPlayer().hurtMarked = true;
            getPlayer().setDeltaMovement(velocity);
            for (int i = 1; i <= Minecraft.getInstance().options.particles().get().getId() + 1; i++) VisualHelper.randomParticleOnModel(getPlayer(), getParticleType(), VisualHelper.ParticleMovement.AWAY);
            if (level == 1 && getPlayer().horizontalCollision) getPlayer().hurt(getPlayer().level().damageSources().flyIntoWall(), 3f);

        } else {
            if (recharge > 0) recharge--;
            if (wasDashing) {
                wasDashing = false;
                FantazicHooks.onDashExpired(getPlayer(), this);
                getPlayer().hurtMarked = true;
                getPlayer().setDeltaMovement(0, 0, 0);
                if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C(""));
            }
        }

        if (duration == initialDur - 5 && initialDur >= 6 && level < 3 && getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C("dash.middle"));

        if (recharged && recharge == 0) {
            recharged = false;
            if (!getPlayer().hasInfiniteMaterials() && getRechargeSound() != null && getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(getRechargeSound()));
        }
    }

    @Override
    public void onTalentUnlock(ITalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("dash/dash1").equals(resLoc) && level < 1) upgrade(1);
        else if (Fantazia.res("dash/dash2").equals(resLoc) && level < 2) upgrade(2);
        else if (Fantazia.res("dash/dash3").equals(resLoc) && level < 3) upgrade(3);
    }
    @Override
    public void onTalentRevoke(ITalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("dash/dash3").equals(resLoc) && level > 2) level = 2;
        else if (Fantazia.res("dash/dash2").equals(resLoc) && level > 1) level = 1;
        else if (Fantazia.res("dash/dash1").equals(resLoc) && level > 0) level = 0;
        
        DashStoneEntity entity = getDashstoneEntity(getPlayer().level());
        if (level == 0 && entity != null) {
            entity.reset();
            dashstoneEntity = -1;
        }
    }
    
    
    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        if (!isDashing()) return;
        if (level <= 1 && !event.getSource().is(FTZDamageTypeTags.NOT_STOPPING_DASH)) stopDash();
        else if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) event.setCanceled(true);
    }

    private void upgrade(int value) {
        level = value;
        if (getRechargeSound() != null && getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(getRechargeSound()));
    }
    public boolean isAvailable() {
        return level > 0;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public boolean isDashing() {
        return this.duration > 0;
    }
    public SimpleParticleType getParticleType() {
        return switch (level) {
            case 1 -> ParticleTypes.POOF;
            default -> null;
        };
    }
    public SoundEvent getDashSound() {
        return switch (level) {
            case 1 -> FTZSoundEvents.DASH1.get();
            case 2 -> FTZSoundEvents.DASH2.get();
            case 3 -> FTZSoundEvents.DASH3.get();
            default -> null;
        };
    }
    public SoundEvent getRechargeSound() {
        return switch (level) {
            case 1 -> FTZSoundEvents.DASH1_RECHARGE.get();
            case 2 -> FTZSoundEvents.DASH2_RECHARGE.get();
            case 3 -> FTZSoundEvents.DASH3_RECHARGE.get();
            default -> null;
        };
    }
    public boolean canDash() {
        return !isDashing() && recharge == 0 && getPlayer().isEffectiveAi() && level > 0 && (getPlayer().onGround() || midAir) && !getPlayer().isSpectator();
    }
    public int getInitDur() {
        return level < 3 ? DEFAULT_DUR : DEFAULT_DUR + 2;
    }
    public int getInitRecharge() {
        return getPlayer().hasInfiniteMaterials() ? 0 : DEFAULT_RECHARGE;
    }
    public int getRecharge() {
        return recharge;
    }
    public int getDur() {
        return duration;
    }
    public void beginDash(Vec3 vec3) {
        if (!canDash()) return;
        StaminaHolder staminaHolder = PlayerAbilityGetter.takeHolder(getPlayer(), StaminaHolder.class);
        if (staminaHolder != null && !staminaHolder.wasteStamina(STAMINA, true, 65)) return;

        int actualDur = FantazicHooks.onDashStart(getPlayer(), this, getInitDur());
        if (actualDur <= 0) return;

        velocity = vec3;
        recharge = getInitRecharge();
        initialRecharge = recharge;
        wasDashing = true;
        recharged = true;

        getPlayer().level().playSound(null, getPlayer().blockPosition(), getDashSound(), SoundSource.PLAYERS);
        getPlayer().resetFallDistance();
        actuallyDash(actualDur);
    }
    public void actuallyDash(int duration) {
        this.duration = duration;
        this.initialDur = duration;
        if (level < 3 && getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C("dash.start"));
    }
    public void stopDash() {
        if (!FantazicHooks.onDashEnd(getPlayer(), this)) return;
        this.duration = 0;
        this.wasDashing = false;
        getPlayer().hurtMarked = true;
        getPlayer().setDeltaMovement(0,0,0);
        if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C(""));
    }
    public void setDashstoneEntity(DashStoneEntity dashstoneEntity) {
        this.dashstoneEntity = dashstoneEntity.getId();
    }
    public @Nullable DashStoneEntity getDashstoneEntity(Level serverLevel) {
        Entity entity = serverLevel.getEntity(dashstoneEntity);
        return entity instanceof DashStoneEntity dashStoneEntity ? dashStoneEntity : null;
    }
}
