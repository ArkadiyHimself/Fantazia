package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class ManaHolder extends PlayerAbilityHolder  {

    private static final float basicRegen = 0.00575f;
    private float mana = getMaxMana();
    private boolean philStone = false;

    boolean toSync = false;

    public ManaHolder(Player player) {
        super(player, Fantazia.location("mana_data"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("mana", mana);
        tag.putBoolean("philStone", philStone);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        mana = compoundTag.getFloat("mana");
        philStone = compoundTag.getBoolean("philStone");
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("mana", mana);
        tag.putBoolean("philStone", philStone);
        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        mana = tag.getFloat("mana");
        philStone = tag.getBoolean("philStone");
    }

    @Override
    public void respawn() {
        restore();
    }

    @Override
    public void serverTick() {
        mana = Math.min(getMaxMana(), mana + getManaRegen());
    }

    @Override
    public void clientTick() {
        mana = Math.min(getMaxMana(), mana + getManaRegen());
    }

    public void wasteMana(float amount) {
        if (!philStone) setMana(Math.max(0, this.mana - amount));
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
        setMana(getMaxMana());
    }

    public void regenerate(float amount, boolean aboveMax) {
        setMana(aboveMax ? mana + amount : Math.min(getMaxMana(), mana + amount));
    }

    public void philStone() {
        philStone = true;
    }

    public boolean hasStone() {
        return philStone;
    }

    public void setMana(float value) {
        this.mana = value;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.manaChanged(serverPlayer, mana);
    }
}
