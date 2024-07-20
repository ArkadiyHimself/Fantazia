package net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities;

import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.AbilityProviding.Talent;
import net.arkadiyhimself.fantazia.api.AttributeRegistry;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.util.Interfaces.INBTsaver;
import net.arkadiyhimself.fantazia.util.Interfaces.IPlayerAbility;
import net.arkadiyhimself.fantazia.util.Interfaces.ITalentRequire;
import net.arkadiyhimself.fantazia.util.Interfaces.ITicking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class StaminaData implements IPlayerAbility, INBTsaver, ITalentRequire, ITicking {
    private static final String ID = "stamina:";
    private final Player owner;
    private float stamina = 20;
    private final float DEFAULT_DELAY = 40;
    private final float regen = 0.1125f;
    private float delay = 0;

    public StaminaData(Player owner) {
        this.owner = owner;
    }

    public float getMaxStamina() {
        return (float) owner.getAttributeValue(AttributeRegistry.MAX_STAMINA.get());
    }
    public float getStamina() {
        return stamina;
    }
    public boolean wasteStamina(float cost, boolean addDelay) {
        return wasteStamina(cost, addDelay, DEFAULT_DELAY);
    }
    public boolean wasteStamina(float cost, boolean addDelay, float customDelay) {
        if (owner.isCreative()) return true;
        if (owner.hasEffect(MobEffectRegistry.FURY.get())) cost *= 0.5f;
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
        FoodData data = owner.getFoodData();
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
        stRegen *= owner.getAttributeValue(AttributeRegistry.STAMINA_REGEN_MULTIPLIER.get());
        return stRegen;
    }
    @Override
    public Player getOwner() {
        return owner;
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
        if (!owner.isSprinting())  {
            delay = Math.max(0, delay - 1);
        } else {
            wasteStamina(0.025f, true, 10);
        }
        if (delay <= 0) {
            stamina = Math.min(getMaxStamina(), stamina + getStaminaRegen());
        }
    }
}
