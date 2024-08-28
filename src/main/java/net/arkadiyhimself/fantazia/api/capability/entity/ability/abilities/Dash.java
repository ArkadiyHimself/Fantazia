package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITalentListener;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
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

public class Dash extends AbilityHolder implements ITalentListener, ITicking, IDamageReacting {
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
    public String ID() {
        return "dash";
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
            for (int i = 1; i <= Minecraft.getInstance().options.particles().get().getId() + 1; i++) VisualHelper.randomParticleOnModel(serverPlayer, getParticleType(), VisualHelper.ParticleMovement.AWAY);
            if (level == 1 && getPlayer().horizontalCollision) getPlayer().hurt(getPlayer().level().damageSources().flyIntoWall(), 3f);

        } else {
            if (recharge > 0) recharge--;
            if (wasDashing) {
                wasDashing = false;
                FTZEvents.onDashExpired(getPlayer(), this);
                getPlayer().hurtMarked = true;
                getPlayer().setDeltaMovement(0, 0, 0);
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), getPlayer());
            }
        }

        if (duration == INITIAL_DUR - 5 && INITIAL_DUR >= 6 && level < 3) NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.middle"), serverPlayer);

        if (recharged && recharge == 0) {
            recharged = false;
            if (!getPlayer().getAbilities().instabuild && getRechargeSound() != null) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(getRechargeSound()), serverPlayer);
        }
    }
    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", this.level);
        tag.putInt("initial_dur", this.INITIAL_DUR);
        tag.putInt("duration", this.duration);
        tag.putInt("initial_recharge", this.INITIAL_RECHARGE);
        tag.putInt("recharge", this.recharge);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        this.level = tag.contains("level") ?  tag.getInt("level") : 0;
        this.INITIAL_DUR = tag.contains("initial_dur") ?  tag.getInt("initial_dur") : 1;
        this.duration = tag.contains("duration") ?  tag.getInt("duration") : 0;
        this.INITIAL_RECHARGE = tag.contains("initial_recharge") ?  tag.getInt("initial_recharge") : 1;
        this.recharge = tag.contains("recharge") ?  tag.getInt("recharge") : 0;
    }
    @Override
    public void onHit(LivingAttackEvent event) {
        if (!isDashing()) return;
        if (level <= 1 && !event.getSource().is(FTZDamageTypeTags.NOT_STOPPING_DASH)) stopDash();
        else if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) event.setCanceled(true);
    }
    @Override
    public void onTalentUnlock(BasicTalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("dash1").equals(resLoc) && level < 1) upgrade(1);
        else if (Fantazia.res("dash2").equals(resLoc) && level < 2) upgrade(2);
        else if (Fantazia.res("dash3").equals(resLoc) && level < 3) upgrade(3);
    }
    @Override
    public void onTalentRevoke(BasicTalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("dash3").equals(resLoc) && level > 2) level = 2;
        else if (Fantazia.res("dash2").equals(resLoc) && level > 1) level = 1;
        else if (Fantazia.res("dash1").equals(resLoc) && level > 0) level = 0;
    }
    private void upgrade(int value) {
        level = value;
        if (getRechargeSound() != null) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(getRechargeSound()), getPlayer());
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
            case 1 -> FTZSoundEvents.DASH_DEFAULT.get();
            case 2 -> FTZSoundEvents.DASH_SECOND.get();
            case 3 -> FTZSoundEvents.DASH_FINAL.get();
        };
    }
    public SoundEvent getRechargeSound() {
        return switch (level) {
            default -> null;
            case 1 -> FTZSoundEvents.DASH1_RECHARGE.get();
            case 2 -> FTZSoundEvents.DASH2_RECHARGE.get();
            case 3 -> FTZSoundEvents.DASH3_RECHARGE.get();
        };
    }
    public boolean canDash() {
        return !isDashing() && recharge == 0 && getPlayer().isEffectiveAi() && level > 0 && (getPlayer().onGround() || midAir) && !getPlayer().isSpectator();
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
        StaminaData staminaData = AbilityGetter.takeAbilityHolder(getPlayer(), StaminaData.class);
        if (staminaData != null && !staminaData.wasteStamina(STAMINA, true, 65)) return;

        int duration = FTZEvents.onDashStart(getPlayer(), this, getInitDur());
        if (duration <= 0) return;

        velocity = vec3;
        recharge = getInitRecharge();
        INITIAL_RECHARGE = recharge;
        wasDashing = true;
        recharged = true;

        getPlayer().level().playSound(null, getPlayer().blockPosition(), getDashSound(), SoundSource.PLAYERS);
        getPlayer().resetFallDistance();
        actuallyDash(duration);
    }
    public void actuallyDash(int duration) {
        this.duration = duration;
        this.INITIAL_DUR = duration;
        if (level < 3) NetworkHandler.sendToPlayer(new PlayAnimationS2C("dash.start"), getPlayer());
    }
    public void stopDash() {
        if (!FTZEvents.onDashEnd(getPlayer(), this)) return;
        this.duration = 0;
        this.wasDashing = false;
        getPlayer().hurtMarked = true;
        getPlayer().setDeltaMovement(0,0,0);
        NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), getPlayer());
    }
}
