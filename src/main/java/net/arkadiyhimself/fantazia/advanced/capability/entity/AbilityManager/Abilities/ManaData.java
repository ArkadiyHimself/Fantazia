package net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.Abilities;

import net.arkadiyhimself.fantazia.advanced.capability.entity.AbilityManager.AbilityHolder;
import net.arkadiyhimself.fantazia.advanced.capacity.AbilityProviding.Talent;
import net.arkadiyhimself.fantazia.registry.AttributeRegistry;
import net.arkadiyhimself.fantazia.util.interfaces.INBTsaver;
import net.arkadiyhimself.fantazia.util.interfaces.IPlayerAbility;
import net.arkadiyhimself.fantazia.util.interfaces.ITalentRequire;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class ManaData extends AbilityHolder implements ITalentRequire, ITicking {
    private static final String ID = "mana:";
    private float mana = 20;
    private boolean philStone = false;
    private final float regen = 0.00575f;

    public ManaData(Player player) {
        super(player);
    }

    public boolean wasteMana(float amount) {
        if (getPlayer().isCreative()) return true;
        float newAmount = mana - amount;
        if (newAmount >= 0) {
            mana = newAmount;
        } else return false;
        return true;
    }
    public float getMana() {
        return mana;
    }
    public float getMaxMana() {
        return (float) getPlayer().getAttributeValue(AttributeRegistry.MAX_MANA.get());
    }
    public float getManaRegen() {
        float basicRegen =  regen;
        FoodData data = getPlayer().getFoodData();
        if (data.getFoodLevel() == 20) {
            if (data.getSaturationLevel() >= 10) {
                basicRegen *= 1.45f;
            } else {
                basicRegen *= 1.25f;
            }
        } else if (data.getFoodLevel() <= 7.5f) {
            basicRegen *= 0.675f;
            if (data.getFoodLevel() <= 3f) {
                basicRegen *= 0.5f;
            }
        }
        return basicRegen;
    }
    public void philStone() {
        philStone = true;
    }
    public boolean hasStone() {
        return philStone;
    }
    @Override
    public void respawn() {
        mana = getMaxMana();
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(ID + "mana", mana);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains(ID + "mana")) mana = tag.getFloat(ID +"mana");
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
        mana = Math.min(getMaxMana(), mana + getManaRegen());
    }
}
