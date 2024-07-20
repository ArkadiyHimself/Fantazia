package net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities;

import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.AbilityProviding.Talent;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.CustomEvents.NewEvents;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.util.Interfaces.IPlayerAbility;
import net.arkadiyhimself.fantazia.util.Interfaces.ITicking;
import net.arkadiyhimself.fantazia.util.Interfaces.INBTsaver;
import net.arkadiyhimself.fantazia.util.Interfaces.ITalentRequire;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.ArrayList;
import java.util.List;

public class Dash implements IPlayerAbility, INBTsaver, ITalentRequire, ITicking {
    private final Player owner;
    private static final String ID = "dash:";
    public static List<ResourceKey<DamageType>> NOT_STOPPING = new ArrayList<>() {{
        add(DamageTypes.CRAMMING);
        add(DamageTypes.DROWN);
        add(DamageTypes.STARVE);
        add(DamageTypes.GENERIC_KILL);
        add(DamageTypes.IN_WALL);
    }};
    private final float STAMINA = 1.5f;
    private final int DEFAULT_DUR = 8;
    private int DEFAULT_RECH = 100;
    private int INITIAL_DUR = 1;
    private int INITIAL_RECH = 1;
    private int duration = 0;
    private int recharge = 0;
    private int level = 0;
    private boolean wasDashing = false;
    private boolean midAir = false;
    private boolean recharged = false;
    private Vec3 velocity = new Vec3(0,0,0);

    public Dash(Player owner) {
        this.owner = owner;
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
            case 1 -> SoundRegistry.DASH_DEFAULT.get();
            case 2 -> SoundRegistry.DASH_SECOND.get();
            case 3 -> SoundRegistry.DASH_FINAL.get();
        };
    }
    public SoundEvent getRechargeSound() {
        return switch (level) {
            default -> null;
            case 1 -> SoundRegistry.DASH1_RECH.get();
            case 2 -> SoundRegistry.DASH2_RECH.get();
            case 3 -> SoundRegistry.DASH3_RECH.get();
        };
    }
    public boolean canDash() {
        return !isDashing() && recharge == 0 && owner.isEffectiveAi()
                && level > 0 && (owner.onGround())
                && !owner.isSpectator();
    }
    public int getInitDur() {
        return DEFAULT_DUR;
    }
    public int getInitRech() {
        if (owner.isCreative()) return 0;
        return DEFAULT_RECH;
    }
    public int getRech() {
        return recharge;
    }
    public int getDur() {
        return duration;
    }
    public void beginDash(Vec3 vec3) {
        if (!canDash()) return;
        if (!(owner instanceof ServerPlayer serverPlayer)) return;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(owner);
        if (abilityManager == null) return;
        StaminaData staminaData = abilityManager.takeAbility(StaminaData.class);
        if (staminaData == null) return;
        if (!staminaData.wasteStamina(STAMINA, true, 65) && !owner.isCreative()) return;
        int duration = NewEvents.onDashStart(serverPlayer, this, getInitDur());
        if (duration > 0) {
            velocity = vec3;
            recharge = getInitRech();
            INITIAL_RECH = recharge;
            wasDashing = true;
            recharged = true;

            owner.level().playSound(null, owner.blockPosition(), getDashSound(), SoundSource.PLAYERS);
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.start"), serverPlayer);
            owner.resetFallDistance();

            if (level < 3) {
                this.duration = duration;
                this.INITIAL_DUR = duration;
            } else {
                int trueDuration = WhereMagicHappens.Abilities.thirdLevelDashDuration(serverPlayer, velocity, 8);
                this.duration = trueDuration;
                this.INITIAL_DUR = trueDuration;
            }
        }
    }
    public void stopDash() {
        if (!owner.level().isClientSide && owner instanceof ServerPlayer serverPlayer) {
            boolean stop = NewEvents.onDashEnd(serverPlayer, this);
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
                for (ResourceKey<DamageType> resourceKey : NOT_STOPPING) {
                    if (event.getSource().is(resourceKey)) {
                        reset = false;
                        break;
                    }
                }
                if (reset) stopDash();
            } else {
                event.setCanceled(true);
            }
        }
    }
    @Override
    public Player getOwner() {
        return owner;
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
        if (!(owner instanceof ServerPlayer serverPlayer)) return;
        if (isDashing()) {
            duration--;
            serverPlayer.hurtMarked = true;
            serverPlayer.setDeltaMovement(velocity);


            int num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 1;
                case DECREASED -> 2;
                case ALL -> 3;
            };
            for (int i = 1; i <= num; i++) {
                WhereMagicHappens.Abilities.randomParticleOnModel(serverPlayer, getParticleType(), WhereMagicHappens.Abilities.ParticleMovement.CHASE_OPPOSITE);
            }
            if (level == 1 && owner.horizontalCollision) {
                owner.hurt(owner.level().damageSources().flyIntoWall(), 3f);
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), serverPlayer);
            }
        }
        if (!isDashing()) {
            if (recharge > 0) { recharge--; }
            if (wasDashing) {
                wasDashing = false;
                NewEvents.onDashExpired(serverPlayer, this);
                serverPlayer.hurtMarked = true;
                serverPlayer.setDeltaMovement(0, 0, 0);
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), serverPlayer);
            }
        }

        if ((duration == INITIAL_DUR - 5 ) && INITIAL_DUR >= 6) {
            NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.middle"), serverPlayer);
        }
        if (recharged && recharge == 0) {
            recharged = false;
            if (!owner.isCreative() && !owner.isSpectator()) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(getRechargeSound()), serverPlayer);
            }
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
}
