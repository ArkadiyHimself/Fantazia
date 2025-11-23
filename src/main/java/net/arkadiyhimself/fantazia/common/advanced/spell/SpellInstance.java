package net.arkadiyhimself.fantazia.common.advanced.spell;

import net.arkadiyhimself.fantazia.common.advanced.cleanse.EffectCleansing;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.common.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.criterion.UseSpellTrigger;
import net.arkadiyhimself.fantazia.data.tags.FTZSpellTags;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class SpellInstance implements INBTSerializable<CompoundTag>, ISyncEveryTick {

    private final LivingEntity livingEntity;
    private final Holder<AbstractSpell> spell;
    private int recharge = 0;
    private int amplifier = 0;
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

    @Override
    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();

        tag.putString("id", spell.value().getID().toString());
        tag.putInt("recharge", recharge);
        tag.putBoolean("available", available);

        return tag;
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        this.recharge = tag.getInt("recharge");
        this.available = tag.getBoolean("available");
    }

    public void serverTick() {
        if (recharge > 0) {
            recharge--;
            if (recharge <= 0 && livingEntity instanceof ServerPlayer serverPlayer) {
                Holder<SoundEvent> holder = spell.value().getRechargeSound();
                if (holder != null) IPacket.playSoundForUI(serverPlayer, holder.value());
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

    public void setAmplifier(int value) {
        this.amplifier = value;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void useResources(SpellCastResult result) {
        if (ActionsHelper.infiniteResources(livingEntity)) return;
        if (result.wasteMana() && livingEntity instanceof Player player) PlayerAbilityHelper.wasteMana(player, spell.value().manacost());
        if (result.recharge()) {
            if (livingEntity.hasEffect(FTZMobEffects.ACE_IN_THE_HOLE) && spell.value() != Spells.ALL_IN.value())
                EffectCleansing.reduceLevel(livingEntity, FTZMobEffects.ACE_IN_THE_HOLE);
            else this.recharge = spell.value().getRecharge(livingEntity);
        }
    }

    public SpellCastResult attemptCast() {
        if (livingEntity.hasEffect(FTZMobEffects.CHAINED) && getSpell().is(FTZSpellTags.IS_CHAINED)) return SpellCastResult.FAIL;
        if (!ActionsHelper.infiniteResources(livingEntity) && (this.recharge > 0 || livingEntity instanceof Player player && !PlayerAbilityHelper.enoughMana(player, spell.value().manacost()))) return SpellCastResult.FAIL;

        SpellCastResult result = SpellCastResult.FREE;

        if (getSpell().value() instanceof SelfSpell selfSpell) result = SpellHelper.trySelfSpell(livingEntity, selfSpell, amplifier, false);
        else if (getSpell().value() instanceof TargetedSpell<?> targetedSpell) result = SpellHelper.tryTargetedSpell(livingEntity, amplifier, targetedSpell);
        else if (getSpell().value() instanceof PassiveSpell passiveSpell) result = SpellHelper.tryPassiveSpell(livingEntity, amplifier, passiveSpell);

        if (livingEntity instanceof ServerPlayer serverPlayer) {
            Prompts.USE_SPELLCAST1.noLongerNeeded(serverPlayer);
            Prompts.USE_SPELLCAST2.noLongerNeeded(serverPlayer);
            Prompts.USE_SPELLCAST3.noLongerNeeded(serverPlayer);

            UseSpellTrigger.INSTANCE.trigger(serverPlayer, getSpell(), result);
        }

        useResources(result);

        return result;
    }

    public void reduceRecharge(int amount) {
        if (this.recharge <= 0) return;
        this.recharge = Math.max(0, this.recharge - amount);
        Holder<SoundEvent> holder = spell.value().getRechargeSound();
        if (recharge <= 0 && livingEntity instanceof ServerPlayer serverPlayer && holder != null) IPacket.playSoundForUI(serverPlayer, holder.value());
    }
}
