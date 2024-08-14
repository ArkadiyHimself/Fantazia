package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.ITalentRequire;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class DoubleJump extends AbilityHolder implements ITalentRequire, ITicking {
    private static final String ID = "double_jump:";
    private boolean unlocked = false;
    private boolean canJump = true;
    private boolean doTick = true;
    private boolean jumped = false;
    private int delay = 0;
    public DoubleJump(Player player) {
        super(player);
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
        if (doTick && delay > 0) delay--;
        if (delay == 0) jumped = false;
        if (!canJump) canJump = getPlayer().onGround();
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(ID + "canJump", this.canJump);
        tag.putBoolean(ID + "unlocked", this.unlocked);
        tag.putBoolean(ID + "jumped", this.jumped);
        tag.putBoolean(ID + "doTick", this.doTick);
        tag.putInt(ID + "delay", this.delay);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        this.canJump = !tag.contains(ID + "canJump") || tag.getBoolean(ID + "canJump");
        this.unlocked = !tag.contains(ID + "unlocked") || tag.getBoolean(ID + "unlocked");
        this.jumped = tag.contains(ID + "jumped") && tag.getBoolean(ID + "jumped");
        this.doTick = !tag.contains(ID + "doTick") || tag.getBoolean(ID + "doTick");
        this.delay = tag.contains(ID + "delay") ? tag.getInt(ID + "delay") : 0;
    }
    @Override
    public void onTalentUnlock(BasicTalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("double_jump").equals(resLoc)) unlock();
    }
    @Override
    public void onTalentRevoke(BasicTalent talent) {
        ResourceLocation resLoc = talent.getID();
        if (Fantazia.res("double_jump").equals(resLoc)) unlocked = false;
    }
    public boolean isUnlocked() {
        return unlocked;
    }
    public boolean canJump() {
        return unlocked && !jumped && canJump && !getPlayer().onGround() && !getPlayer().isFallFlying() && !getPlayer().hasEffect(MobEffects.LEVITATION) && !getPlayer().hasEffect(MobEffects.SLOW_FALLING);
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
        if (canJump() && getPlayer() instanceof ServerPlayer serverPlayer) {
            AbilityHelper.doubleJump(serverPlayer);
            canJump = false;
            serverPlayer.resetFallDistance();
        }
    }
    public void unlock() {
        unlocked = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundEvents.ZOMBIE_VILLAGER_CURE), serverPlayer);
    }
}
