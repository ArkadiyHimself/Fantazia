package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class ManaHolder extends PlayerAbilityHolder  {
    private static final float basicRegen = 0.00575f;
    private float mana = getMaxMana();
    private boolean philStone = false;

    public ManaHolder(Player player) {
        super(player, Fantazia.res("mana_data"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("mana", mana);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        if (compoundTag.contains("mana")) mana = compoundTag.getFloat("mana");
    }

    @Override
    public CompoundTag syncSerialize() {
        return serializeNBT(getPlayer().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        deserializeNBT(getPlayer().registryAccess(), tag);
    }

    @Override
    public void respawn() {
        mana = getMaxMana();
    }

    @Override
    public void tick() {
        mana = Math.min(getMaxMana(), mana + getManaRegen());
    }

    public boolean wasteMana(float amount) {
        if (getPlayer().hasInfiniteMaterials() || philStone) return true;
        float newAmount = mana - amount;
        if (newAmount < 0) return false;
        mana = newAmount;
        return true;
    }

    public float getMana() {
        return mana;
    }

    public float getMaxMana() {
        return (float) getPlayer().getAttributeValue(FTZAttributes.MAX_MANA);
    }

    public float getManaRegen() {
        float basicRegen = ManaHolder.basicRegen;
        FoodData data = getPlayer().getFoodData();
        if (data.getFoodLevel() == 20) {
            if (data.getSaturationLevel() >= 10) basicRegen *= 1.45f;
            else basicRegen *= 1.25f;
        } else if (data.getFoodLevel() <= 7.5f) {
            basicRegen *= 0.675f;
            if (data.getFoodLevel() <= 3f) basicRegen *= 0.5f;
        }
        return basicRegen;
    }

    public void restore() {
        mana = getMaxMana();
    }

    public void regenerate(float value) {
        this.mana = org.joml.Math.min(getMaxMana(), mana + value);
    }

    public void philStone() {
        philStone = true;
    }

    public boolean hasStone() {
        return philStone;
    }
}
