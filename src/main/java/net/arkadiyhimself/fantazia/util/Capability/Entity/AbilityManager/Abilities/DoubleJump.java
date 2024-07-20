package net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities;

import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.AbilityProviding.Talent;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.CustomEvents.NewEvents;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.util.Interfaces.INBTsaver;
import net.arkadiyhimself.fantazia.util.Interfaces.IPlayerAbility;
import net.arkadiyhimself.fantazia.util.Interfaces.ITalentRequire;
import net.arkadiyhimself.fantazia.util.Interfaces.ITicking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DoubleJump implements IPlayerAbility, INBTsaver, ITalentRequire, ITicking {
    private static final String ID = "double_jump:";
    private final Player owner;
    private boolean unlocked = true;
    private boolean canJump = true;
    private boolean doTick = true;
    private boolean jumped = false;
    private int delay = 0;

    public DoubleJump(Player owner) {
        this.owner = owner;
    }

    public boolean isUnlocked() {
        return unlocked;
    }
    public boolean canJump() {
        return unlocked && !jumped && canJump && !owner.onGround() && !owner.isFallFlying()
                && !owner.hasEffect(MobEffects.LEVITATION) && !owner.hasEffect(MobEffects.SLOW_FALLING);
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
        if (canJump() && owner instanceof ServerPlayer serverPlayer) {
            doubleJump(serverPlayer);
            canJump = false;
            serverPlayer.resetFallDistance();
        }
    }
    public static void doubleJump(ServerPlayer serverPlayer) {
        AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
        if (abilityManager == null) return;
        StaminaData staminaData = abilityManager.takeAbility(StaminaData.class);
        if (staminaData == null) return;
        boolean doJump = staminaData.wasteStamina(1.75f, true);
        if (!doJump) return;

        boolean event = NewEvents.onDoubleJump(serverPlayer);
        if (event) {
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS);
            Vec3 vec3 = serverPlayer.getDeltaMovement();
            serverPlayer.setDeltaMovement(vec3.x, 0.64 + serverPlayer.getJumpBoostPower(), vec3.z);
            serverPlayer.fallDistance = -2f;
            serverPlayer.hurtMarked = true;
        }
    }
    @Override
    public Player getOwner() {
        return owner;
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
            canJump = owner.onGround();
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
