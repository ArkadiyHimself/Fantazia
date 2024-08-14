package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITalentRequire;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class Dash extends AbilityHolder implements ITalentRequire, ITicking, IDamageReacting {
    private static final String ID = "dash:";
    private static final float STAMINA = 1.5f;
    private static final int DEFAULT_DUR = 7;
    private static final int DEFAULT_RECHARGE = 100;
    private int INITIAL_DUR = 1;
    private int INITIAL_RECHARGE = 1;
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
        tag.putInt(ID + "initial_recharge", this.INITIAL_RECHARGE);
        tag.putInt(ID + "recharge", this.recharge);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.level = tag.contains(ID + "level") ?  tag.getInt(ID + "level") : 0;
        this.INITIAL_DUR = tag.contains(ID + "initial_dur") ?  tag.getInt(ID + "initial_dur") : 1;
        this.duration = tag.contains(ID + "duration") ?  tag.getInt(ID + "duration") : 0;
        this.INITIAL_RECHARGE = tag.contains(ID + "initial_recharge") ?  tag.getInt(ID + "initial_recharge") : 1;
        this.recharge = tag.contains(ID + "recharge") ?  tag.getInt(ID + "recharge") : 0;
    }
    @Override
    public void onTalentUnlock(BasicTalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("dash1").equals(resLoc) && level < 1) level = 1;
        else if (Fantazia.res("dash2").equals(resLoc) && level < 2) level = 2;
        else if (Fantazia.res("dash3").equals(resLoc) && level < 3) level = 3;
    }
    @Override
    public void onTalentRevoke(BasicTalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("dash3").equals(resLoc) && level > 2) level = 2;
        else if (Fantazia.res("dash2").equals(resLoc) && level > 1) level = 1;
        else if (Fantazia.res("dash1").equals(resLoc) && level > 0) level = 0;
    }
    @Override
    public void onHit(LivingAttackEvent event) {
        if (!isDashing()) return;
        if (level <= 1 && !event.getSource().is(FTZDamageTypeTags.NOT_STOPPING_DASH)) stopDash();
        else if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) event.setCanceled(true);
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
            case 1 -> FTZSoundEvents.DASH1_RECHARGE;
            case 2 -> FTZSoundEvents.DASH2_RECHARGE;
            case 3 -> FTZSoundEvents.DASH3_RECHARGE;
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
    public int getInitRecharge() {
        return getPlayer().isCreative() ? 0 : DEFAULT_RECHARGE;
    }
    public int getRecharge() {
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
            recharge = getInitRecharge();
            INITIAL_RECHARGE = recharge;
            wasDashing = true;
            recharged = true;

            getPlayer().level().playSound(null, getPlayer().blockPosition(), getDashSound(), SoundSource.PLAYERS);
            getPlayer().resetFallDistance();
            actuallyDash(duration);
        }
    }
    public void actuallyDash(int duration) {
        this.duration = duration;
        this.INITIAL_DUR = duration;
        if (level < 3) NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.start"), getPlayer());
    }
    public void stopDash() {
        if (!getPlayer().level().isClientSide() && getPlayer() instanceof ServerPlayer serverPlayer) {
            boolean stop = FTZEvents.onDashEnd(serverPlayer, this);
            if (stop) {
                this.duration = 0;
                this.wasDashing = false;
                serverPlayer.hurtMarked = true;
                serverPlayer.setDeltaMovement(0,0,0);
            }
        }
    }
}
