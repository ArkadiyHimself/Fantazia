package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.criterion.EuphoriaTrigger;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZGameRules;
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

public class EuphoriaHolder extends PlayerAbilityHolder implements IDamageEventListener {

    public static final int TICKS = 400;
    public static final Function<LivingEntity, Float> MODIFIER =livingEntity -> {
        EuphoriaHolder euphoriaHolder = livingEntity instanceof Player player ? PlayerAbilityHelper.takeHolder(player, EuphoriaHolder.class) : null;
        return euphoriaHolder == null ? 0f : (float) euphoriaHolder.comboPercent();
    };
    private static final AttributeModifier LIFESTEAL = new AttributeModifier(Fantazia.res("talent.euphoria.lifesteal"),0.25, AttributeModifier.Operation.ADD_VALUE);

    private int remainingTicks = 0;
    private int peakTicks = 0;
    private int kills = 0;
    private boolean relentless = false;
    private boolean savage = false;

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
        tag.putBoolean("savage", savage);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.remainingTicks = tag.getInt("remaining");
        this.peakTicks = tag.getInt("peakTicks");
        this.kills = tag.getInt("kills");
        this.relentless = tag.getBoolean("relentless");
        this.savage = tag.getBoolean("savage");
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("remainingTicks", remainingTicks);
        tag.putInt("kills", kills);
        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        this.remainingTicks = tag.getInt("remainingTicks");
        this.kills = tag.getInt("kills");
    }

    @Override
    public void serverTick() {
        if (!getPlayer().level().getGameRules().getBoolean(FTZGameRules.EUPHORIA)) {
            reset();
            return;
        }
        if (remainingTicks > 0) {
            remainingTicks--;
            if (getPlayer() instanceof ServerPlayer serverPlayer && kills > 1) EuphoriaTrigger.INSTANCE.trigger(serverPlayer, remainingTicks, peakTicks, kills);
        } else kills = 0;

        if (kills >= 10) peakTicks++;
        else peakTicks = 0;

        AttributeInstance lifesteal = getPlayer().getAttribute(FTZAttributes.LIFESTEAL);
        if (lifesteal != null) {
            if (kills >= 10 && savage && !lifesteal.hasModifier(LIFESTEAL.id())) lifesteal.addPermanentModifier(LIFESTEAL);
            else if ((kills < 10 || !savage) && lifesteal.hasModifier(LIFESTEAL.id())) lifesteal.removeModifier(LIFESTEAL);
        }
    }

    @Override
    public void clientTick() {
        if (remainingTicks > 0) remainingTicks--;
        else kills = 0;

        if (kills >= 10) peakTicks++;
        else peakTicks = 0;
    }

    @Override
    public void respawn() {
        reset();
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (!source.is(FTZDamageTypes.REMOVAL) && event.getNewDamage() > 0) remainingTicks = Math.max(0, remainingTicks - 80);
    }

    public void processAttack(LivingDamageEvent.Pre event) {
        if (!getPlayer().level().getGameRules().getBoolean(FTZGameRules.EUPHORIA)) return;
        if (relentless) {
            CriticalHitEvent criticalHitEvent = CommonHooks.fireCriticalHit(getPlayer(), event.getEntity(), false, comboPercent() + 1f);
            float dmg = event.getNewDamage();
            float multiplier = criticalHitEvent.getDamageMultiplier();
            event.setNewDamage(dmg * multiplier);
        }
    }

    public void increase() {
        if (!getPlayer().level().getGameRules().getBoolean(FTZGameRules.EUPHORIA)) return;
        this.kills = Math.min(10, kills + 1);
        this.remainingTicks = this.kills == 10 ? TICKS * 2 : TICKS;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.increaseEuphoria(serverPlayer);
    }

    public int kills() {
        if (!getPlayer().level().getGameRules().getBoolean(FTZGameRules.EUPHORIA)) return 0;
        return kills;
    }

    public int ticks() {
        return remainingTicks;
    }

    public float comboPercent() {
        if (!getPlayer().level().getGameRules().getBoolean(FTZGameRules.EUPHORIA)) return 0;
        return kills > 1 ? (float) kills / 10 : 0f;
    }

    public void reset() {
        remainingTicks = 0;
        peakTicks = 0;
        kills = 0;
        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.resetEuphoria(serverPlayer);
    }

    public void setSavage(boolean value) {
        this.savage = value;
    }

    public void setRelentless(boolean value) {
        this.relentless = value;
    }
}
