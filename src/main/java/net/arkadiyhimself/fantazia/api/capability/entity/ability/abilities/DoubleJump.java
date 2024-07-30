package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.advanced.capacity.abilityproviding.Talent;
import net.arkadiyhimself.fantazia.api.capability.ITalentRequire;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class DoubleJump extends AbilityHolder implements ITalentRequire, ITicking {
    private static final String ID = "double_jump:";
    private boolean unlocked = true;
    private boolean canJump = true;
    private boolean doTick = true;
    private boolean jumped = false;
    private int delay = 0;

    public DoubleJump(Player player) {
        super(player);
    }

    public boolean isUnlocked() {
        return unlocked;
    }
    public boolean canJump() {
        return unlocked && !jumped && canJump && !getPlayer().onGround() && !getPlayer().isFallFlying()
                && !getPlayer().hasEffect(MobEffects.LEVITATION) && !getPlayer().hasEffect(MobEffects.SLOW_FALLING);
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

    @Override
    public void respawn() {
        canJump = true;
        doTick = true;
        jumped = false;
        delay = 0;
    }

    @Override
    public void tick() {
        if (doTick && delay > 0) {
            delay--;
        }
        if (delay == 0) {
            jumped = false;
        }
        if (!canJump) {
            canJump = getPlayer().onGround();
        }
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
    public Talent required() {
        return null;
    }
    @Override
    public void onTalentUnlock(Talent talent) {
    }
}
