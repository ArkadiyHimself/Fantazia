package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.ITalentListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.criterion.EuphoriaTrigger;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

public class EuphoriaHolder extends PlayerAbilityHolder implements IDamageEventListener, ITalentListener {

    public static final int TICKS = 400;
    public static final Function<LivingEntity, Float> MODIFIER =livingEntity -> {
        EuphoriaHolder euphoriaHolder = livingEntity instanceof Player player ? PlayerAbilityGetter.takeHolder(player, EuphoriaHolder.class) : null;
        return euphoriaHolder == null ? 0f : (float) euphoriaHolder.comboPercent();
    };
    private static final AttributeModifier LIFESTEAL = new AttributeModifier(Fantazia.res("talent.euphoria.lifesteal"),0.25, AttributeModifier.Operation.ADD_VALUE);

    private int remainingTicks = 0;
    private int peakTicks = 0;
    private int kills = 0;
    private boolean relentless = false;

    public EuphoriaHolder(@NotNull Player player) {
        super(player, Fantazia.res("euphoria"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("remaining", remainingTicks);
        tag.putInt("peakTicks", peakTicks);
        tag.putInt("kills", kills);
        tag.putBoolean("relentless", relentless);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.remainingTicks = tag.getInt("remaining");
        this.peakTicks = tag.getInt("peakTicks");
        this.kills = tag.getInt("kills");
        this.relentless = tag.getBoolean("relentless");
    }

    @Override
    public void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
            if (getPlayer() instanceof ServerPlayer serverPlayer && kills > 1) EuphoriaTrigger.INSTANCE.trigger(serverPlayer, remainingTicks, peakTicks, kills);
        } else kills = 0;

        if (kills >= 10) peakTicks++;
        else peakTicks = 0;

        AttributeInstance lifesteal = getPlayer().getAttribute(FTZAttributes.LIFESTEAL);
        if (lifesteal != null) {
            boolean savage = TalentHelper.hasTalent(getPlayer(), Fantazia.res("euphoria_boost/savagery"));

            if (kills >= 10 && savage && !lifesteal.hasModifier(LIFESTEAL.id())) lifesteal.addPermanentModifier(LIFESTEAL);
            else if ((kills < 10 || !savage) && lifesteal.hasModifier(LIFESTEAL.id())) lifesteal.removeModifier(LIFESTEAL);
        }
    }

    @Override
    public void respawn() {
        reset();
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (!source.is(FTZDamageTypes.REMOVAL)) remainingTicks = Math.max(0, remainingTicks - 80);
    }

    @Override
    public void onTalentUnlock(ITalent talent) {
         if (talent.getID().equals(Fantazia.res("euphoria_boost/relentless"))) relentless = true;
    }

    @Override
    public void onTalentRevoke(ITalent talent) {
        if (talent.getID().equals(Fantazia.res("euphoria_boost/relentless"))) relentless = false;
    }

    public void processAttack(LivingDamageEvent.Pre event) {
        if (relentless) {
            CriticalHitEvent criticalHitEvent = CommonHooks.fireCriticalHit(getPlayer(), event.getEntity(), false, comboPercent() + 1f);
            float dmg = event.getNewDamage();
            float multiplier = criticalHitEvent.getDamageMultiplier();
            event.setNewDamage(dmg * multiplier);
        }
    }

    public void increase() {
        this.kills = Math.min(10, kills + 1);
        this.remainingTicks = TICKS;
    }

    public int kills() {
        return kills;
    }

    public int ticks() {
        return remainingTicks;
    }

    public float comboPercent() {
        return kills > 1 ? (float) kills / 10 : 0f;
    }

    public void reset() {
        remainingTicks = 0;
        peakTicks = 0;
        kills = 0;
    }
}
