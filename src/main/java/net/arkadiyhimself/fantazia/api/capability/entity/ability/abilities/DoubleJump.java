package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.ITalentListener;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class DoubleJump extends AbilityHolder implements ITalentListener, ITicking {
    public static final int ELYTRA_RECHARGE = 350;
    private int recharge = 0;
    private boolean unlocked = false;
    private boolean canJump = true;
    private boolean doTick = true;
    private boolean jumped = false;
    private int delay = 0;
    public DoubleJump(Player player) {
        super(player);
    }
    @Override
    public String id() {
        return "double_jump";
    }
    @Override
    public void respawn() {
        canJump = true;
        doTick = true;
        jumped = false;
        delay = 0;

    }

    @Override
    public void tick() {
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
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("recharge", this.recharge);
        tag.putBoolean("canJump", this.canJump);
        tag.putBoolean("unlocked", this.unlocked);
        tag.putBoolean("jumped", this.jumped);
        tag.putBoolean("doTick", this.doTick);
        tag.putInt("delay", this.delay);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        this.recharge = tag.contains("recharge") ? tag.getInt("recharge") : 0;
        this.canJump = !tag.contains("canJump") || tag.getBoolean( "canJump");
        this.unlocked = !tag.contains("unlocked") || tag.getBoolean("unlocked");
        this.jumped = tag.contains("jumped") && tag.getBoolean("jumped");
        this.doTick = !tag.contains("doTick") || tag.getBoolean("doTick");
        this.delay = tag.contains("delay") ? tag.getInt("delay") : 0;
    }
    @Override
    public void onTalentUnlock(BasicTalent talent) {
        if (Fantazia.res("double_jump").equals(talent.getID())) unlock();
    }
    @Override
    public void onTalentRevoke(BasicTalent talent) {
        if (Fantazia.res("double_jump").equals(talent.getID())) unlocked = false;
    }
    public boolean isUnlocked() {
        return unlocked;
    }
    public boolean canJump() {
        return recharge <= 0 && unlocked && !jumped && canJump && !getPlayer().onGround() && !getPlayer().hasEffect(MobEffects.LEVITATION) && !getPlayer().hasEffect(MobEffects.SLOW_FALLING);
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
            if (!AbilityHelper.accelerateFlying(getPlayer())) return;
            recharge = ELYTRA_RECHARGE;
        } else {
            if (!AbilityHelper.doubleJump(getPlayer())) return;
            canJump = false;
            getPlayer().resetFallDistance();
        }
    }
    public void unlock() {
        unlocked = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundEvents.ZOMBIE_VILLAGER_CURE), serverPlayer);
    }
    public int getRecharge() {
        return recharge;
    }
}
