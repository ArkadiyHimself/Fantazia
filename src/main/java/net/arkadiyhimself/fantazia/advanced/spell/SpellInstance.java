package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
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

    private final LivingEntity livingEntity;
    private final Holder<AbstractSpell> spell;
    private int recharge = 0;
    private boolean available;

    public SpellInstance(Holder<AbstractSpell> spell, LivingEntity livingEntity) {
        this.spell = spell;
        this.livingEntity = livingEntity;
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

    public void tick() {
        if (recharge > 0) {
            recharge--;
            if (recharge == 0) {
                Holder<SoundEvent> holder = spell.value().getRechargeSound();
                if (holder != null) livingEntity.level().playSound(null, livingEntity.blockPosition(), holder.value(), SoundSource.PLAYERS);
            }
        }

        if (available) spell.value().tryToTick(livingEntity,recharge > 0);
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

    public void useResources() {
        if (ActionsHelper.infiniteResources(livingEntity)) return;
        if (livingEntity instanceof Player player) PlayerAbilityHelper.wasteMana(player, spell.value().getManacost());
        this.recharge = spell.value().getRecharge(livingEntity);
    }

    public boolean attemptCast() {
        if (!ActionsHelper.infiniteResources(livingEntity) && (this.recharge > 0 || livingEntity instanceof Player player && !PlayerAbilityHelper.enoughMana(player, spell.value().getManacost()))) return false;

        boolean flag = false;

        if (getSpell().value() instanceof SelfSpell selfSpell) flag = SpellHelper.trySelfSpell(livingEntity, selfSpell, false);
        else if (getSpell().value() instanceof TargetedSpell<?> targetedSpell) flag = SpellHelper.tryTargetedSpell(livingEntity, targetedSpell);
        else if (getSpell().value() instanceof PassiveSpell passiveSpell) flag = SpellHelper.tryPassiveSpell(livingEntity, passiveSpell);

        if (flag) useResources();

        return flag;
    }

    public void reduceRecharge(int amount) {
        this.recharge = Math.max(0, this.recharge - amount);
    }
}
