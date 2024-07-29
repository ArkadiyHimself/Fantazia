package net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capacity.abilityproviding.Talent;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.interfaces.ITalentRequire;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.ArrayList;
import java.util.List;

public class Dash extends AbilityHolder implements ITalentRequire, ITicking {
    private static final String ID = "dash:";
    public static List<ResourceKey<DamageType>> NOT_STOPPING = new ArrayList<>() {{
        add(DamageTypes.CRAMMING);
        add(DamageTypes.DROWN);
        add(DamageTypes.STARVE);
        add(DamageTypes.GENERIC_KILL);
        add(DamageTypes.IN_WALL);
    }};
    private final float STAMINA = 1.5f;
    private final int DEFAULT_DUR = 7;
    private final int DEFAULT_RECH = 100;
    private int INITIAL_DUR = 1;
    private int INITIAL_RECH = 1;
    private int duration = 0;
    private int recharge = 0;
    private int level = 0;
    private boolean wasDashing = false;
    private boolean midAir = false;
    private boolean recharged = false;
    private Vec3 velocity = new Vec3(0,0,0);

    public Dash(Player player) {
        super(player);
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
        if (!(getPlayer() instanceof ServerPlayer serverPlayer)) return;
        if (isDashing()) {
            duration--;
            serverPlayer.hurtMarked = true;
            serverPlayer.setDeltaMovement(velocity);
            int num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 1;
                case DECREASED -> 2;
                case ALL -> 3;
            };
            for (int i = 1; i <= num; i++) VisualHelper.randomParticleOnModel(serverPlayer, getParticleType(), VisualHelper.ParticleMovement.CHASE_OPPOSITE);
            if (level == 1 && getPlayer().horizontalCollision) {
                getPlayer().hurt(getPlayer().level().damageSources().flyIntoWall(), 3f);
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), serverPlayer);
            }
        } else {
            if (recharge > 0) recharge--;
            if (wasDashing) {
                wasDashing = false;
                FTZEvents.onDashExpired(serverPlayer, this);
                serverPlayer.hurtMarked = true;
                serverPlayer.setDeltaMovement(0, 0, 0);
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), serverPlayer);
            }
        }

        if ((duration == INITIAL_DUR - 5) && INITIAL_DUR >= 6 && level < 3) NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.middle"), serverPlayer);

        if (recharged && recharge == 0) {
            recharged = false;
            if (!getPlayer().isCreative() && !getPlayer().isSpectator()) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(getRechargeSound()), serverPlayer);
        }
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(ID + "level", this.level);
        tag.putInt(ID + "initial_dur", this.INITIAL_DUR);
        tag.putInt(ID + "duration", this.duration);
        tag.putInt(ID + "initial_rech", this.INITIAL_RECH);
        tag.putInt(ID + "recharge", this.recharge);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.level = tag.contains(ID + "level") ?  tag.getInt(ID + "level") : 0;
        this.INITIAL_DUR = tag.contains(ID + "initial_dur") ?  tag.getInt(ID + "initial_dur") : 1;
        this.duration = tag.contains(ID + "duration") ?  tag.getInt(ID + "duration") : 0;
        this.INITIAL_RECH = tag.contains(ID + "initial_rech") ?  tag.getInt(ID + "initial_rech") : 1;
        this.recharge = tag.contains(ID + "recharge") ?  tag.getInt(ID + "recharge") : 0;
    }
    @Override
    public Talent required() {
        return null;
    }
    @Override
    public void onTalentUnlock(Talent talent) {
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
            default -> null;
            case 1 -> ParticleTypes.POOF;
        };
    }
    public SoundEvent getDashSound() {
        return switch (level) {
            default -> null;
            case 1 -> FTZSoundEvents.DASH_DEFAULT;
            case 2 -> FTZSoundEvents.DASH_SECOND;
            case 3 -> FTZSoundEvents.DASH_FINAL;
        };
    }
    public SoundEvent getRechargeSound() {
        return switch (level) {
            default -> null;
            case 1 -> FTZSoundEvents.DASH1_RECH;
            case 2 -> FTZSoundEvents.DASH2_RECH;
            case 3 -> FTZSoundEvents.DASH3_RECH;
        };
    }
    public boolean canDash() {
        return !isDashing() && recharge == 0 && getPlayer().isEffectiveAi()
                && level > 0 && (getPlayer().onGround())
                && !getPlayer().isSpectator();
    }
    public int getInitDur() {
        return level < 3 ? DEFAULT_DUR : DEFAULT_DUR + 2;
    }
    public int getInitRech() {
        return getPlayer().isCreative() ? 0 : DEFAULT_RECH;
    }
    public int getRech() {
        return recharge;
    }
    public int getDur() {
        return duration;
    }
    public void beginDash(Vec3 vec3) {
        if (!canDash()) return;
        if (!(getPlayer() instanceof ServerPlayer serverPlayer)) return;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(getPlayer());
        if (abilityManager == null) return;
        StaminaData staminaData = abilityManager.takeAbility(StaminaData.class);
        if (staminaData == null) return;
        if (!staminaData.wasteStamina(STAMINA, true, 65) && !getPlayer().isCreative()) return;
        int duration = FTZEvents.onDashStart(serverPlayer, this, getInitDur());
        if (duration > 0) {
            velocity = vec3;
            recharge = getInitRech();
            INITIAL_RECH = recharge;
            wasDashing = true;
            recharged = true;

            getPlayer().level().playSound(null, getPlayer().blockPosition(), getDashSound(), SoundSource.PLAYERS);

            getPlayer().resetFallDistance();

            if (level < 3) actuallyDash(duration);
            else actuallyDash(AbilityHelper.leapDuration(serverPlayer, vec3, duration));
        }
    }
    public void actuallyDash(int duration) {
        this.duration = duration;
        this.INITIAL_DUR = duration;
        if (level < 3) NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.start"), getPlayer());
    }
    public void stopDash() {
        if (!getPlayer().level().isClientSide && getPlayer() instanceof ServerPlayer serverPlayer) {
            boolean stop = FTZEvents.onDashEnd(serverPlayer, this);
            if (stop) {
                this.duration = 0;
                this.wasDashing = false;
            }
        }
    }
    public void onHit(LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer && isDashing()) {
            if (level <= 1) {
                boolean reset = true;
                for (ResourceKey<DamageType> resourceKey : NOT_STOPPING)
                    if (event.getSource().is(resourceKey)) {
                        reset = false;
                        break;
                    }
                if (reset) stopDash();
            } else event.setCanceled(true);
        }
    }
}
