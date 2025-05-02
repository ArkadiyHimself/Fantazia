package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class StaminaHolder extends PlayerAbilityHolder {

    private static final float DEFAULT_DELAY = 40;
    private static final float DEFAULT_REGEN = 0.1125f;

    private float stamina = 20;
    private float delay = 0;

    public StaminaHolder(Player player) {
        super(player, Fantazia.res("stamina_data"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("stamina", stamina);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        if (compoundTag.contains("stamina")) stamina = compoundTag.getFloat("stamina");
    }

    @Override
    public void respawn() {
        stamina = getMaxStamina();
        delay = 0;
    }

    @Override
    public void serverTick() {
        if (!getPlayer().isSprinting()) delay = Math.max(0, delay - 1);
        else wasteStamina(0.00625f, true, 10);
        if (delay <= 0) stamina = Math.min(getMaxStamina(), stamina + getStaminaRegen());
    }

    @Override
    public void clientTick() {}

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

    public boolean wasteStamina(float cost, boolean addDelay, float customDelay) {
        if (getPlayer().hasInfiniteMaterials()) return true;
        if (getPlayer().hasEffect(FTZMobEffects.FURY)) cost *= 0.5f;
        float newST = stamina - cost;
        if (newST > 0) {
            stamina = newST;
            if (addDelay) delay = Math.max(customDelay, delay);
            return true;
        }
        return false;
    }

    public float getStaminaRegen() {
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
        this.stamina = Math.min(getMaxStamina(), stamina + value);
    }

    public void restore() {
        stamina = getMaxStamina();
    }
}
