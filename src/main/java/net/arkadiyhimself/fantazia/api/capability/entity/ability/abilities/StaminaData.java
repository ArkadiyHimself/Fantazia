package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.advanced.capacity.abilityproviding.Talent;
import net.arkadiyhimself.fantazia.api.capability.ITalentRequire;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class StaminaData extends AbilityHolder implements ITalentRequire, ITicking {
    private static final String ID = "stamina:";
    private float stamina = 20;
    private final float DEFAULT_DELAY = 40;
    private final float regen = 0.1125f;
    private float delay = 0;

    public StaminaData(Player player) {
        super(player);
    }
    @Override
    public void respawn() {
        stamina = getMaxStamina();
        delay = 0;
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(ID + "stamina", stamina);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains(ID + "stamina")) stamina = tag.getFloat(ID +"stamina");
    }
    @Override
    public Talent required() {
        return null;
    }
    @Override
    public void onTalentUnlock(Talent talent) {

    }
    @Override
    public void tick() {
        if (!getPlayer().isSprinting())  {
            delay = Math.max(0, delay - 1);
        } else {
            wasteStamina(0.025f, true, 10);
        }
        if (delay <= 0) {
            stamina = Math.min(getMaxStamina(), stamina + getStaminaRegen());
        }
    }

    public float getMaxStamina() {
        return (float) getPlayer().getAttributeValue(FTZAttributes.MAX_STAMINA);
    }
    public float getStamina() {
        return stamina;
    }
    public boolean wasteStamina(float cost, boolean addDelay) {
        return wasteStamina(cost, addDelay, DEFAULT_DELAY);
    }
    public boolean wasteStamina(float cost, boolean addDelay, float customDelay) {
        if (getPlayer().isCreative()) return true;
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
        float stRegen = regen;
        FoodData data = getPlayer().getFoodData();
        if (data.getFoodLevel() >= 17.5f) {
            if (data.getSaturationLevel() >= 10) {
                stRegen *= 1.45f;
            } else {
                stRegen *= 1.25f;
            }
        } else if (data.getFoodLevel() <= 7.5f) {
            stRegen *= 0.675f;
            if (data.getFoodLevel() <= 3f) {
                stRegen *= 0.5f;
            }
        }
        stRegen *= getPlayer().getAttributeValue(FTZAttributes.STAMINA_REGEN_MULTIPLIER);
        return stRegen;
    }
    public void restore() {
        stamina = getMaxStamina();
    }
}
