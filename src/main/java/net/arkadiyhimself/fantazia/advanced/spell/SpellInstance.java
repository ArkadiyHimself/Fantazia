package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.ManaHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class SpellInstance implements INBTSerializable<CompoundTag> {
    private final Holder<AbstractSpell> spell;
    private int recharge = 0;
    private boolean available;

    public SpellInstance(Holder<AbstractSpell> spell) {
        this.spell = spell;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putString("id", spell.value().getID().toString());
        tag.putInt("recharge", recharge);
        tag.putBoolean("available", available);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.recharge = tag.getInt("recharge");
        this.available = tag.getBoolean("available");
    }

    public void tick(LivingEntity entity) {
        if (recharge > 0) {
            recharge--;
            if (recharge == 0) {
                Holder<SoundEvent> holder = spell.value().getRechargeSound();
                if (holder != null) entity.level().playSound(null, entity.blockPosition(), holder.value(), SoundSource.PLAYERS);
            }
        }

        if (available) spell.value().tryToTick(entity,recharge > 0);
    }

    public Holder<AbstractSpell> getSpell() {
        return spell;
    }

    public int recharge() {
        return recharge;
    }

    public void resetRecharge() {
        recharge = 0;
    }

    public void setAvailable(boolean value) {
        this.available = value;
    }

    public boolean isAvailable() {
        return available;
    }

    public void putOnRecharge() {
        this.recharge = spell.value().getRecharge();
    }

    public boolean attemptCast(Player player) {
        boolean notInf = !player.hasInfiniteMaterials();
        if (this.recharge > 0 && notInf) return false;

        boolean flag = false;

        if (this.getSpell().value() instanceof SelfSpell selfSpell) flag = SpellHelper.trySelfSpell(player, selfSpell, false);
        else if (this.getSpell().value() instanceof TargetedSpell<?> targetedSpell) flag = SpellHelper.tryTargetedSpell(player, targetedSpell);
        else if (this.getSpell().value() instanceof PassiveSpell) flag = true;

        if (flag && notInf) putOnRecharge();

        return flag;
    }
}