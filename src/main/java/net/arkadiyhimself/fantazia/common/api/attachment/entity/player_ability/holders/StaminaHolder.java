package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class StaminaHolder extends PlayerAbilityHolder {

    private static final int DEFAULT_DELAY = 40;
    private static final float DEFAULT_REGEN = 0.1125f;

    private float stamina = 20;
    private int delay = 0;

    public StaminaHolder(Player player) {
        super(player, Fantazia.location("stamina_data"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("stamina", this.stamina);
        tag.putInt("delay", this.delay);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.stamina = compoundTag.getFloat("stamina");
        this.delay = compoundTag.getInt("delay");
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("stamina", this.stamina);
        tag.putInt("delay", this.delay);
        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        this.stamina = tag.getFloat("stamina");
        this.delay = tag.getInt("delay");
    }

    @Override
    public void respawn() {
        setStamina(getMaxStamina(), 0);
    }

    @Override
    public void serverTick() {
        if (!getPlayer().isSprinting()) delay = Math.max(0, delay - 1);
        else wasteStamina(0.00125f, true, 10);
        if (delay <= 0) stamina = Math.min(getMaxStamina(), stamina + getStaminaRegen());
    }

    @Override
    public void clientTick() {
        if (!getPlayer().isSprinting()) delay = Math.max(0, delay - 1);
        else wasteStamina(0.00125f, true, 10);
        if (delay <= 0) stamina = Math.min(getMaxStamina(), stamina + getStaminaRegen());
    }

    public float getMaxStamina() {
        return (float) getPlayer().getAttributeValue(FTZAttributes.MAX_STAMINA);
    }

    public float getStamina() {
        return stamina;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean wasteStamina(float cost, boolean addDelay) {
        return wasteStamina(cost, addDelay, DEFAULT_DELAY);
    }

    public boolean wasteStamina(float cost, boolean addDelay, int customDelay) {
        if (getPlayer().hasInfiniteMaterials()) return true;
        if (getPlayer().hasEffect(FTZMobEffects.FURY)) cost *= 0.5f;
        float newST = stamina - cost;
        if (newST > 0) {
            if (addDelay) setStamina(newST, Math.max(customDelay, delay));
            else setStamina(newST);
            return true;
        }
        return false;
    }

    private float getStaminaRegen() {
        float stRegen = DEFAULT_REGEN;
        FoodData data = getPlayer().getFoodData();
        if (data.getFoodLevel() >= 17.5f) {
            if (data.getSaturationLevel() >= 10) stRegen *= 1.45f;
            else stRegen *= 1.25f;
        } else if (data.getFoodLevel() <= 7.5f) {
            stRegen *= 0.675f;
            if (data.getFoodLevel() <= 3f) stRegen *= 0.5f;
        }
        return stRegen;
    }

    public void recover(float value) {
        setStamina(Math.min(getMaxStamina(), stamina + value));
    }

    public void restore() {
        setStamina(getMaxStamina());
    }

    public void setStamina(float value, int delay) {
        this.stamina = value;
        this.delay = delay;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.staminaChanged(serverPlayer, this.stamina, this.delay);
    }

    public void setStamina(float value) {
        this.stamina = value;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.staminaChanged(serverPlayer, this.stamina, this.delay);
    }
}
