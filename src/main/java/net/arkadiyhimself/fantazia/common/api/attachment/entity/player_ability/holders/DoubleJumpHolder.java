package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class DoubleJumpHolder extends PlayerAbilityHolder {

    public static final int ELYTRA_RECHARGE = 350;

    private boolean unlocked = false;
    private boolean boostElytra = false;
    private int recharge = 0;
    private boolean canJump = true;
    private boolean doTick = true;
    private boolean jumped = false;
    private int delay = 0;
    public boolean enabled = true;

    public DoubleJumpHolder(Player player) {
        super(player, Fantazia.location("double_jump"));
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
        tag.putBoolean("enabled", this.enabled);
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
        this.enabled = compoundTag.getBoolean("enabled");
    }

    @Override
    public CompoundTag serializeInitial() {
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
    public void deserializeInitial(CompoundTag compoundTag) {
        this.recharge = compoundTag.getInt("recharge");
        this.canJump = !compoundTag.contains("canJump") || compoundTag.getBoolean( "canJump");
        this.unlocked = compoundTag.getBoolean("unlocked");
        this.boostElytra = compoundTag.getBoolean("boostElytra");
        this.jumped = compoundTag.contains("jumped") && compoundTag.getBoolean("jumped");
        this.doTick = !compoundTag.contains("doTick") || compoundTag.getBoolean("doTick");
        this.delay = compoundTag.getInt("delay");;
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
    public void clientTick() {
        serverTick();
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean canJump() {
        return recharge <= 0 && unlocked && enabled && !jumped && canJump && !getPlayer().mayFly() && !getPlayer().onGround()
                && !getPlayer().isInLiquid() && !getPlayer().hasEffect(MobEffects.LEVITATION) &&
                !getPlayer().hasEffect(MobEffects.SLOW_FALLING);
    }

    public void regularJump() {
        this.doTick = false;
        this.delay = 1;
        this.jumped = true;
        if (unlocked && getPlayer() instanceof ServerPlayer serverPlayer) Prompts.USE_DOUBLE_JUMP.maybePromptPlayer(serverPlayer);
    }

    public void buttonRelease() {
        this.doTick = true;
        if (getPlayer().level().isClientSide()) IPacket.jumpButtonRelease();
    }

    public void tryToJumpClient() {
        if (!canJump()) return;
        boolean flying = getPlayer().isFallFlying();
        if (flying) {
            if (!boostElytra || !PlayerAbilityHelper.accelerateFlying(getPlayer())) return;
        }
        else {
            ItemStack stack = getPlayer().getItemBySlot(EquipmentSlot.CHEST);
            if (stack.canElytraFly(getPlayer())) return;
            if (!PlayerAbilityHelper.doubleJump(getPlayer())) return;
            getPlayer().resetFallDistance();
        }
        successfulJump(flying);
        IPacket.performDoubleJump(flying);
    }

    public void successfulJump(boolean flying) {
        if (flying) {
            if (!getPlayer().hasInfiniteMaterials()) recharge = elytraRecharge();
        } else canJump = false;
        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.DOUBLE_JUMP.value(), SoundSource.PLAYERS);
        if (getPlayer() instanceof ServerPlayer serverPlayer) Prompts.USE_DOUBLE_JUMP.noLongerNeeded(serverPlayer);
    }

    public void unlock() {
        unlocked = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DOUBLE_JUMP_UNLOCKED.value());
    }

    public void lock() {
        unlocked = false;
    }

    public void setBoostElytra(boolean value) {
        this.boostElytra = value;
    }

    // I love hollow knight
    public void pogo() {
        canJump = true;
    }

    public int elytraRecharge() {
        if (getPlayer().hasInfiniteMaterials()) return 0;
        AttributeInstance instance = getPlayer().getAttribute(FTZAttributes.RECHARGE_MULTIPLIER);
        return (int) (ELYTRA_RECHARGE * (instance == null ? 1f : instance.getValue() / 100));
    }

    public int getRecharge() {
        return recharge;
    }

    public void setEnabled() {
        this.enabled = true;
    }

    public void setDisabled() {
        this.enabled = false;
    }
}
