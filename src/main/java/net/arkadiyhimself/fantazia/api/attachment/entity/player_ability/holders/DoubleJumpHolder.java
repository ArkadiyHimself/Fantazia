package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.api.type.entity.ITalentListener;
import net.arkadiyhimself.fantazia.data.talent.types.BasicTalent;
import net.arkadiyhimself.fantazia.networking.packets.stuff.PlaySoundForUIS2C;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class DoubleJumpHolder extends PlayerAbilityHolder implements ITalentListener {
    public static final int ELYTRA_RECHARGE = 350;
    private int recharge = 0;
    private boolean unlocked = false;
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
        tag.putBoolean("jumped", this.jumped);
        tag.putBoolean("doTick", this.doTick);
        tag.putInt("delay", this.delay);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.recharge = compoundTag.contains("recharge") ? compoundTag.getInt("recharge") : 0;
        this.canJump = !compoundTag.contains("canJump") || compoundTag.getBoolean( "canJump");
        this.unlocked = !compoundTag.contains("unlocked") || compoundTag.getBoolean("unlocked");
        this.jumped = compoundTag.contains("jumped") && compoundTag.getBoolean("jumped");
        this.doTick = !compoundTag.contains("doTick") || compoundTag.getBoolean("doTick");
        this.delay = compoundTag.contains("delay") ? compoundTag.getInt("delay") : 0;
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
            if (!PlayerAbilityHelper.accelerateFlying(getPlayer())) return;
            recharge = ELYTRA_RECHARGE;
        } else {
            if (!PlayerAbilityHelper.doubleJump(getPlayer())) return;
            canJump = false;
            getPlayer().resetFallDistance();
        }
    }
    public void unlock() {
        unlocked = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(SoundEvents.ZOMBIE_VILLAGER_CURE));
    }
    public int getRecharge() {
        return recharge;
    }
}
