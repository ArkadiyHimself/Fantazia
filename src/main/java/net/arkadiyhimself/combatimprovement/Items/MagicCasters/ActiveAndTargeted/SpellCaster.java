package net.arkadiyhimself.combatimprovement.Items.MagicCasters.ActiveAndTargeted;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.api.AttributeRegistry;
import net.arkadiyhimself.combatimprovement.util.Interfaces.ISpellCaster;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SpellCaster extends Item implements ISpellCaster {
    protected final Component abilityName;
    private final SoundEvent castSound;
    public final Ability type;
    public final boolean goThruWalls;
    public int MAX_RECH;
    public double TARGET_CAST_RANGE;
    public final float MANACOST;
    public boolean reflectable = true;
    public SpellCaster(int recharge, SoundEvent castSound, String itemName, Ability ability, double range, boolean goThruWalls, float manaCost) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.abilityName = Component.translatable("tooltip.combatimprovement." + itemName + ".ability");
        this.MAX_RECH = recharge;
        this.castSound = castSound;
        this.type = ability;
        this.TARGET_CAST_RANGE = range;
        this.goThruWalls = goThruWalls;
        this.MANACOST = manaCost;
    }
    public double getCastRange(@Nullable ServerPlayer player) {
        double range = TARGET_CAST_RANGE;
        if (player == null) {
            return range;
        } else {
            range *= player.getAttributeValue(AttributeRegistry.CAST_RANGE_BASE_MULTIPLIER.get());
            range += player.getAttributeValue(AttributeRegistry.CAST_RANGE_ADDITION.get());
            range *= player.getAttributeValue(AttributeRegistry.CAST_RANGE_TOTAL_MULTIPLIER.get());
            return range;
        }
    }
    public enum Ability {
        TARGETED, SELF
    }
    @Override
    public boolean conditionNotMet(ServerPlayer player) {
        return true;
    }
    @Override
    public boolean targetConditions(ServerPlayer player, LivingEntity target) { return false; }

    @Override
    public SoundEvent getCastSound() { return this.castSound; }
    @Override
    public boolean hasCooldown(ServerPlayer player) {
        return player.getCooldowns().isOnCooldown(this);
    }
    @Override
    public void activeAbility(@NotNull ServerPlayer player) {
        player.getCooldowns().addCooldown(this, MAX_RECH);
        player.level().playSound(null, player.blockPosition(), getCastSound(), SoundSource.PLAYERS);
    }
    @Override
    public boolean targetedAbility(LivingEntity caster, LivingEntity target, boolean wasDeflected) {
        if (caster instanceof ServerPlayer player) {
            if (!player.isCreative()) {
                player.getCooldowns().addCooldown(this, MAX_RECH);
            }
        }
        target.level().playSound(null, caster.blockPosition(), getCastSound(), SoundSource.NEUTRAL);
        if (wasDeflected) {
            return true;
        }
        WhereMagicHappens.Abilities.TargetedResult result = WhereMagicHappens.Abilities.checkForAbilityBlocking(target);
        if (result == WhereMagicHappens.Abilities.TargetedResult.REFLECTED) {
            if (reflectable) {
                this.targetedAbility(target, caster, true);
            }
        }
        return result == WhereMagicHappens.Abilities.TargetedResult.DEFAULT;
    }
}
