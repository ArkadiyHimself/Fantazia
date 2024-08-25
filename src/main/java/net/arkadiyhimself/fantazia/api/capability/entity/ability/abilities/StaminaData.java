package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.api.capability.ITalentListener;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class StaminaData extends AbilityHolder implements ITalentListener, ITicking {
    private static final float DEFAULT_DELAY = 40;
    private static final float defaultRegen = 0.1125f;
    private float stamina = 20;
    private float delay = 0;

    public StaminaData(Player player) {
        super(player);
    }

    @Override
    public String ID() {
        return "stamina";
    }

    @Override
    public void respawn() {
        stamina = getMaxStamina();
        delay = 0;
    }
    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("stamina", stamina);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        if (tag.contains("stamina")) stamina = tag.getFloat("stamina");
    }
    @Override
    public void onTalentUnlock(BasicTalent talent) {

    }
    @Override
    public void onTalentRevoke(BasicTalent talent) {

    }

    @Override
    public void tick() {
        if (!getPlayer().isSprinting()) delay = Math.max(0, delay - 1);
        else wasteStamina(0.025f, true, 10);
        if (delay <= 0) stamina = Math.min(getMaxStamina(), stamina + getStaminaRegen());

    }
    public float getMaxStamina() {
        return (float) getPlayer().getAttributeValue(FTZAttributes.MAX_STAMINA.get());
    }
    public float getStamina() {
        return stamina;
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean wasteStamina(float cost, boolean addDelay) {
        return wasteStamina(cost, addDelay, DEFAULT_DELAY);
    }
    public boolean wasteStamina(float cost, boolean addDelay, float customDelay) {
        if (getPlayer().isCreative()) return true;
        if (getPlayer().hasEffect(FTZMobEffects.FURY.get())) cost *= 0.5f;
        float newST = stamina - cost;
        if (newST > 0) {
            stamina = newST;
            if (addDelay) delay = Math.max(customDelay, delay);
            return true;
        }
        return false;
    }
    public float getStaminaRegen() {
        float stRegen = defaultRegen;
        FoodData data = getPlayer().getFoodData();
        if (data.getFoodLevel() >= 17.5f) {
            if (data.getSaturationLevel() >= 10) stRegen *= 1.45f;
            else stRegen *= 1.25f;
        } else if (data.getFoodLevel() <= 7.5f) {
            stRegen *= 0.675f;
            if (data.getFoodLevel() <= 3f) stRegen *= 0.5f;
        }
        stRegen *= getPlayer().getAttributeValue(FTZAttributes.STAMINA_REGEN_MULTIPLIER.get());
        return stRegen;
    }
    public void restore() {
        stamina = getMaxStamina();
    }
}
