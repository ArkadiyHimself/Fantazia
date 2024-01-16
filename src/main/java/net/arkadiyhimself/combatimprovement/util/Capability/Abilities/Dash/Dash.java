package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.NewEvents;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff.Haemorrhage;
import net.arkadiyhimself.combatimprovement.Registries.SoundRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;

public class Dash extends PlayerCapability {
    public Dash(Player player) {
        super(player);
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.player.getId(), AttachDash.DASH_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("dashLevel", this.dashLevel);
        tag.putInt("dashDuration", this.dashDuration);
        tag.putInt("dashRecharge", this.dashRecharge);
        tag.putDouble("xVelocity", this.xVelocity);
        tag.putDouble("zVelocity", this.zVelocity);
        tag.putBoolean("startedDash", this.startedDash);
        tag.putBoolean("canDashInAir", this.canDashInAir);
        tag.putBoolean("dashRecharged", this.dashRecharged);
        tag.putInt("initialDur", this.initialDur);
        return tag;
    }

    public static List<DamageSource> notStopDashing = new ArrayList<>(){{
        add(DamageSource.STARVE);
        add(DamageSource.DROWN);
        add(DamageSource.IN_FIRE);
        add(DamageSource.ON_FIRE);
        add(DamageSource.LAVA);
        add(Haemorrhage.BLEEDING);
    }};
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.dashLevel = nbt.contains("dashLevel") ? nbt.getInt("dashLevel") : 0;
        this.dashDuration = nbt.contains("dashDuration") ? nbt.getInt("dashDuration") : 0;
        this.dashRecharge = nbt.contains("dashRecharge") ? nbt.getInt("dashRecharge") : 0;
        this.startedDash = nbt.contains("startedDash") && nbt.getBoolean("startedDash");
        this.xVelocity = nbt.contains("xVelocity") ? nbt.getDouble("xVelocity") : 0;
        this.zVelocity = nbt.contains("zVelocity") ? nbt.getDouble("zVelocity") : 0;
        this.canDashInAir = nbt.contains("canDashInAir") && nbt.getBoolean("canDashInAir");
        this.dashRecharged = nbt.contains("dashRecharged") && nbt.getBoolean("dashRecharged");
        this.initialDur = nbt.contains("initialDur") ? nbt.getInt("initialDur") : 0;
    }
    private final float stCost = 1.5f;
    private int MAX_DASH_DUR = 8;
    private int dashDuration = 0;
    private int MAX_DASH_RECH = 100;
    private int dashRecharge = 0;
    public boolean startedDash = false;
    public double xVelocity;
    public double zVelocity;
    public int dashLevel = 0;
    public boolean canDashInAir = false;
    public boolean dashRecharged = false;
    public int initialDur = 0;
    public boolean isDashing() { return getDur() > 0; }
    public void ticking(ServerPlayer serverPlayer) {
        if (isDashing()) {
            serverPlayer.hurtMarked = true;
            serverPlayer.setDeltaMovement(xVelocity, 0.1, zVelocity);
            int num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 1;
                case DECREASED -> 2;
                case ALL -> 3;
            };
            for (int i = 1; i <= num; i++) {
                WhereMagicHappens.Abilities.createRandomParticleOnHumanoid(serverPlayer, getParticleType(), WhereMagicHappens.Abilities.ParticleMovement.CHASE_OPPOSITE);
            }
            if (dashLevel == 1 && player.horizontalCollision) {
                player.hurt(DamageSource.FLY_INTO_WALL, 3f);
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), serverPlayer);
            }
        }
        if (isDashing()) {
            dashDuration--;
        }
        if (getDur() == 0) {
            if (dashRecharge > 0) { dashRecharge--; }
            if (startedDash) {
                startedDash = false;
                NewEvents.onDashExpired(serverPlayer);
                serverPlayer.hurtMarked = true;
                serverPlayer.setDeltaMovement(0, 0, 0);
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), serverPlayer);
            }
        }

        if ((getDur() == initialDur - 5 ) && initialDur >= 6) {
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.middle"), serverPlayer);
        }
        if (dashRecharged && dashRecharge == 0) {
            dashRecharged = false;
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(getRechargeSound()), serverPlayer);
        }
        updateTracking();
    }
    public void setDashLevel(int level) {
        dashLevel = level;
        updateTracking();
    }
    public SimpleParticleType getParticleType() {
        return switch (dashLevel) {
            default -> null;
            case 1 -> ParticleTypes.POOF;
        };
    }
    public SoundEvent getDashSound() {
        return switch (dashLevel) {
            default -> null;
            case 1 -> SoundRegistry.DASH_DEFAULT.get();
            case 2 -> SoundRegistry.DASH_SECOND.get();
            case 3 -> SoundRegistry.DASH_FINAL.get();
        };
    }
    public SoundEvent getRechargeSound() {
        return switch (dashLevel) {
            default -> null;
            case 1 -> SoundRegistry.DASH1_RECH.get();
            case 2 -> SoundRegistry.DASH2_RECH.get();
            case 3 -> SoundRegistry.DASH3_RECH.get();
        };
    }
    public void startDash(double xr, double zr, ServerPlayer serverPlayer) {
        if (!canDash()) { return; }
        int duration = NewEvents.onDashStart(serverPlayer, getMaxDuration());
        if (duration > 0) {
            xVelocity = xr;
            zVelocity = zr;
            dashRecharge = getMaxRecharge();
            startedDash = true;
            dashRecharged = true;
            serverPlayer.level.playSound(null, serverPlayer.blockPosition(), getDashSound(), SoundSource.PLAYERS);
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.start"), serverPlayer);
            serverPlayer.resetFallDistance();
            if (dashLevel != 3) {
                dashDuration = duration;
                initialDur = duration;
            } else {
                int trueDuration = WhereMagicHappens.Abilities.thirdLevelDashDurationHorizontal(serverPlayer, xVelocity, zVelocity, 8);
                dashDuration = trueDuration;
                initialDur = trueDuration;
            }
            if (!player.isCreative()) {
                AttachDataSync.get(player).ifPresent(dataSync -> {
                    dataSync.wasteStamina(stCost, true);
                });
            }
        }
        updateTracking();
    }
    public void stopDash(ServerPlayer serverPlayer) {
        if (!player.level.isClientSide) {
            boolean stop = NewEvents.onDashEnd(serverPlayer);
            if (stop) {
                dashDuration = 0;
                startedDash = false;
            }
        }
    }
    public int getRecharge() {
        return dashRecharge;
    }
    public int getMaxRecharge() {
        if (player.isCreative()) return 0;
        return MAX_DASH_RECH;
    }
    public int getDur() {
        return dashDuration;
    }
    public int getMaxDuration() {
        return MAX_DASH_DUR; }
    public boolean canDash() {
        return !isDashing() && dashRecharge == 0 && player.isEffectiveAi()
                && dashLevel > 0 && (player.isOnGround() || canDashInAir)
                && !player.isSpectator() && AttachDataSync.getUnwrap(player).stamina >= stCost;
    }
    public void onHit(LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (dashLevel <= 1 && isDashing() && !notStopDashing.contains(event.getSource())) {
                stopDash(serverPlayer);
            } else if (isDashing()) {
                event.setCanceled(true);
            }
            updateTracking();
        }
    }
}
