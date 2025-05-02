package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.ITalentListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class DoubleJumpHolder extends PlayerAbilityHolder implements ITalentListener {

    public static final int ELYTRA_RECHARGE = 350;

    private boolean unlocked = false;
    private boolean boostElytra = false;
    private int recharge = 0;
    private boolean canJump = true;
    private boolean doTick = true;
    private boolean jumped = false;
    private int delay = 0;

    public DoubleJumpHolder(Player player) {
        super(player, Fantazia.res("double_jump"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("recharge", this.recharge);
        tag.putBoolean("canJump", this.canJump);
        tag.putBoolean("unlocked", this.unlocked);
        tag.putBoolean("boostElytra", this.boostElytra);
        tag.putBoolean("jumped", this.jumped);
        tag.putBoolean("doTick", this.doTick);
        tag.putInt("delay", this.delay);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.recharge = compoundTag.getInt("recharge");
        this.canJump = !compoundTag.contains("canJump") || compoundTag.getBoolean( "canJump");
        this.unlocked = compoundTag.getBoolean("unlocked");
        this.boostElytra = compoundTag.getBoolean("boostElytra");
        this.jumped = compoundTag.contains("jumped") && compoundTag.getBoolean("jumped");
        this.doTick = !compoundTag.contains("doTick") || compoundTag.getBoolean("doTick");
        this.delay = compoundTag.getInt("delay");
    }

    @Override
    public void respawn() {
        canJump = true;
        doTick = true;
        jumped = false;
        delay = 0;
    }

    @Override
    public void serverTick() {
        if (recharge > 0) recharge--;

        if (getPlayer().isFallFlying()) {
            doTick = false;
            delay = 0;
            jumped = false;
            canJump = true;
            return;
        }

        if (doTick && delay > 0) delay--;
        if (delay == 0) jumped = false;
        if (!canJump) canJump = getPlayer().onGround();
        if (getPlayer().onGround()) recharge = 0;
    }

    @Override
    public void onTalentUnlock(ITalent talent) {
        if (Fantazia.res("aerial/double_jump").equals(talent.getID())) unlock();
        if (Fantazia.res("aerial/finished_wings").equals(talent.getID())) boostElytra = true;
    }

    @Override
    public void onTalentRevoke(ITalent talent) {
        if (Fantazia.res("aerial/double_jump").equals(talent.getID())) unlocked = false;
        if (Fantazia.res("aerial/finished_wings").equals(talent.getID())) boostElytra = false;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean canJump() {
        return recharge <= 0 && unlocked && !jumped && canJump && !getPlayer().getAbilities().flying && !getPlayer().onGround() && !getPlayer().isInLiquid() &&
                !getPlayer().hasEffect(MobEffects.LEVITATION) && !getPlayer().hasEffect(MobEffects.SLOW_FALLING);
    }

    public void regularJump() {
        this.doTick = false;
        this.delay = 1;
        this.jumped = true;
    }

    public void buttonRelease() {
        this.doTick = true;
    }

    public void tryToJump() {
        if (!canJump()) return;
        if (getPlayer().isFallFlying()) {
            if (!boostElytra || !PlayerAbilityHelper.accelerateFlying(getPlayer())) return;
            recharge = ELYTRA_RECHARGE;
        } else {
            if (!PlayerAbilityHelper.doubleJump(getPlayer())) return;
            canJump = false;
            getPlayer().resetFallDistance();
        }
    }

    public void unlock() {
        unlocked = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.soundForUI(serverPlayer, FTZSoundEvents.DOUBLE_JUMP_UNLOCKED.value());
    }

    public void pogo() {
        canJump = true;
    }

    public int getRecharge() {
        return recharge;
    }
}
