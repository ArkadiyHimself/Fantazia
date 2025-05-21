package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.entities.DashStone;
import net.arkadiyhimself.fantazia.events.FantazicHooks;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.Runes;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.UUID;

public class DashHolder extends PlayerAbilityHolder implements IDamageEventListener {

    private static final float STAMINA = 1.5f;
    private static final int DEFAULT_DUR = 7;
    private static final int DEFAULT_RECHARGE = 100;

    private int level = 0;
    private int initialDur = 1;
    private int duration = 0;
    private int initialRecharge = 1;
    private int recharge = 0;
    private boolean wasDashing = false;
    private boolean wasRecharging = false;
    private Vec3 velocity = new Vec3(0,0,0);
    private @Nullable UUID dashstoneEntityServer = null;
    private int dashstoneEntityClient = -1;

    private boolean syncedEntity = false;

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
        tag.putBoolean("wasDashing", this.wasDashing);
        tag.putBoolean("wasRecharging", this.wasRecharging);

        tag.putDouble("dX", velocity.x);
        tag.putDouble("dY", velocity.y);
        tag.putDouble("dZ", velocity.z);

        if (dashstoneEntityServer != null) tag.putUUID("entity", dashstoneEntityServer);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.level = tag.getInt("level");
        this.initialDur = tag.contains("initial_dur") ? tag.getInt("initial_dur") : 1;
        this.duration = tag.getInt("duration");
        this.initialRecharge = tag.contains("initial_recharge") ?  tag.getInt("initial_recharge") : 1;
        this.recharge = tag.getInt("recharge");
        this.wasDashing = tag.getBoolean("wasDashing");
        this.wasRecharging = tag.getBoolean("wasRecharging");

        this.velocity = new Vec3(tag.getDouble("dX"), tag.getDouble("dY"), tag.getDouble("dZ"));

        if (tag.contains("entity")) this.dashstoneEntityServer = tag.getUUID("entity");
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();

        tag.putInt("level", this.level);
        tag.putInt("initial_dur", this.initialDur);
        tag.putInt("duration", this.duration);
        tag.putInt("initial_recharge", this.initialRecharge);
        tag.putInt("recharge", this.recharge);
        tag.putBoolean("wasDashing", this.wasDashing);
        tag.putBoolean("wasRecharging", this.wasRecharging);

        tag.putDouble("dX", this.velocity.x);
        tag.putDouble("dY", this.velocity.y);
        tag.putDouble("dZ", this.velocity.z);

        tag.putInt("entity", this.dashstoneEntityClient);

        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        this.level = tag.getInt("level");
        this.initialDur = tag.contains("initial_dur") ? tag.getInt("initial_dur") : 1;
        this.duration = tag.getInt("duration");
        this.initialRecharge = tag.getInt("initial_recharge");
        this.recharge = tag.getInt("recharge");
        this.wasDashing = tag.getBoolean("wasDashing");
        this.wasRecharging = tag.getBoolean("wasRecharging");

        this.velocity = new Vec3(tag.getDouble("dX"), tag.getDouble("dY"), tag.getDouble("dZ"));

        this.dashstoneEntityClient = tag.getInt("entity");
    }

    @Override
    public void respawn() {
        duration = 0;
        recharge = 0;
        wasDashing = false;
        wasRecharging = false;
    }

    @Override
    public void serverTick() {
        if (duration > 0) {
            duration--;
            getPlayer().move(MoverType.SELF, velocity);
            VisualHelper.particleOnEntityServer(getPlayer(), getParticleType(), ParticleMovement.AWAY, 3);
            if (level == 1 && getPlayer().horizontalCollision) {
                boolean hurt = getPlayer().hurt(getPlayer().level().damageSources().flyIntoWall(), 3f);
                if (hurt) stopDash();
            }
            if (piercer()) {
                AABB aabb = getPlayer().getBoundingBox().inflate(0.4, 0, 0.4);
                List<LivingEntity> targets = getPlayer().level().getEntitiesOfClass(LivingEntity.class, aabb);
                targets.removeIf(entity -> entity == getPlayer());

                AttributeInstance attackDamage = getPlayer().getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackDamage != null) attackDamage.addTransientModifier(new AttributeModifier(Fantazia.res("dash_debuff"), -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                for (LivingEntity livingEntity : targets) getPlayer().attack(livingEntity);
                if (attackDamage != null) attackDamage.removeModifier(Fantazia.res("dash_debuff"));
            }
            if (duration == 0) {
                FantazicHooks.onDashExpired(getPlayer(),this);
                if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.animatePlayer(serverPlayer, "");;
            }
        } else {
            if (recharge > 0) recharge--;
        }

        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            if (duration == initialDur - 5 && initialDur >= 6 && level < 3) IPacket.animatePlayer(serverPlayer, "dash.middle");;

            if (!syncedEntity && getPlayer().level() instanceof ServerLevel serverLevel && dashstoneEntityServer != null) {
                Entity entity = serverLevel.getEntity(dashstoneEntityServer);
                if (!(entity instanceof DashStone dashStone)) return;
                IPacket.setDashStoneEntity(serverPlayer, dashStone.getId());
                syncedEntity = true;
            }
        }
    }

    @Override
    public void clientTick() {
        if (duration > 0) {
            duration--;
            getPlayer().setDeltaMovement(velocity);
            if (duration == 0) {
                FantazicHooks.onDashExpired(getPlayer(), this);
                getPlayer().setDeltaMovement(0, 0, 0);
            }
        } else {
            if (recharge > 0) {
                recharge--;
                if (recharge <= 0 && getRechargeSound() != null) FantazicUtil.playSoundUI(getPlayer(), getRechargeSound(),1f,1f);
            }
        }
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        if (!isDashing()) return;
        if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && level > 1) event.setCanceled(true);
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        if (!isDashing()) return;
        if (level <= 1 && !event.getSource().is(FTZDamageTypeTags.NOT_STOPPING_DASH)) stopDash();
    }

    public void upgrade() {
        this.level = Math.min(this.level + 1, 3);
        if (getRechargeSound() != null && getPlayer() instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, getRechargeSound());
    }

    public void downgrade() {
        this.level = Math.max(this.level - 1, 0);
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

    public @Nullable SoundEvent getRechargeSound() {
        return switch (level) {
            case 1 -> FTZSoundEvents.DASH1_RECHARGE.get();
            case 2 -> FTZSoundEvents.DASH2_RECHARGE.get();
            case 3 -> FTZSoundEvents.DASH3_RECHARGE.get();
            default -> null;
        };
    }

    public boolean canDash() {
        return !isDashing() && recharge == 0 && getPlayer().isEffectiveAi() && level > 0 && (getPlayer().onGround() || aerobat()) && !getPlayer().isSpectator();
    }

    public int getInitDur() {
        return level < 3 ? DEFAULT_DUR : DEFAULT_DUR + 2;
    }

    public int getInitRecharge() {
        if (getPlayer().hasInfiniteMaterials()) return 0;
        AttributeInstance instance = getPlayer().getAttribute(FTZAttributes.RECHARGE_MULTIPLIER);
        return (int) (DEFAULT_RECHARGE * (instance == null ? 1f : instance.getValue() / 100));
    }

    public int getRecharge() {
        return recharge;
    }

    public int getDur() {
        return duration;
    }

    public void beginDash() {
        if (!canDash()) return;
        StaminaHolder staminaHolder = PlayerAbilityHelper.takeHolder(getPlayer(), StaminaHolder.class);
        if (staminaHolder != null && !staminaHolder.wasteStamina(STAMINA, true, 65)) return;

        int actualDur = FantazicHooks.onDashStart(getPlayer(), this, getInitDur());
        if (actualDur <= 0) return;

        velocity = PlayerAbilityHelper.dashDeltaMovement(getPlayer(), 1.8f, !omnidirectional());
        recharge = getInitRecharge();
        initialRecharge = recharge;
        wasDashing = true;
        if (!getPlayer().hasInfiniteMaterials()) wasRecharging = true;

        getPlayer().level().playSound(null, getPlayer().blockPosition(), getDashSound(), SoundSource.PLAYERS);
        getPlayer().resetFallDistance();
        actuallyDash(actualDur);

        if (getPlayer().level().isClientSide()) IPacket.beginDash();
    }

    public void actuallyDash(int duration) {
        this.duration = duration;
        this.initialDur = duration;
        if (level < 3 && getPlayer() instanceof ServerPlayer serverPlayer) IPacket.animatePlayer(serverPlayer, "dash.start");;
    }

    public void stopDash() {
        if (!FantazicHooks.onDashEnd(getPlayer(), this)) return;
        if (duration > 1) getPlayer().move(MoverType.SELF, velocity);

        this.duration = 0;
        this.wasDashing = false;
        getPlayer().hurtMarked = true;
        getPlayer().setDeltaMovement(0,0,0);
        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            IPacket.animatePlayer(serverPlayer, "");
            IPacket.stopDash(serverPlayer);
        };
    }

    public void setDashstoneEntityServer(DashStone dashStone) {
        this.dashstoneEntityServer = dashStone.getUUID();
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.setDashStoneEntity(serverPlayer, dashStone.getId());
    }

    public void setDashstoneEntityClient(int id) {
        this.dashstoneEntityClient = id;
    }

    public @Nullable DashStone getDashstoneEntity(Level level) {
        Entity entity = null;
        if (level instanceof ServerLevel serverLevel && dashstoneEntityServer != null) entity = serverLevel.getEntity(dashstoneEntityServer);
        else if (level instanceof ClientLevel clientLevel) entity = clientLevel.getEntity(dashstoneEntityClient);
        return entity instanceof DashStone dashStone ? dashStone : null;
    }

    public void resetDashstoneEntity() {
        this.dashstoneEntityServer = null;
        this.dashstoneEntityClient = -1;
        DashStone entity = getDashstoneEntity(getPlayer().level());
        if (entity != null) entity.reset();
    }

    public void dashStoneEntityTouched() {
        this.dashstoneEntityServer = null;
        this.dashstoneEntityClient = -1;
    }

    public void pogo() {
        this.recharge = 0;
    }

    private boolean omnidirectional() {
        return FantazicUtil.hasRune(getPlayer(), Runes.OMNIDIRECTIONAL);
    }

    private boolean aerobat() {
        return FantazicUtil.hasRune(getPlayer(), Runes.AEROBAT);
    }

    private boolean piercer() {
        return FantazicUtil.hasRune(getPlayer(), Runes.PIERCER);
    }
}
