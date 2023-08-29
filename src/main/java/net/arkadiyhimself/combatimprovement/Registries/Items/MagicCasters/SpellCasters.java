package net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters;

import net.arkadiyhimself.combatimprovement.util.Interfaces.ISpellCaster;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class SpellCasters extends Item implements ISpellCaster {
    protected final Component abilityName;
    private final SoundEvent castSound;
    public final Ability type;
    public int MAX_RECH;
    public double TARGET_CAST_RANGE;
    public SpellCasters(int recharge, SoundEvent castSound, Component abilityName, Ability ability, double range) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.MAX_RECH = recharge;
        this.castSound = castSound;
        this.abilityName = abilityName;
        this.type = ability;
        this.TARGET_CAST_RANGE = range;
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
        player.level.playSound(null, player.blockPosition(), getCastSound(), SoundSource.PLAYERS);
    }
    @Override
    public void targetedAbility(ServerPlayer player, LivingEntity target) {
        if (player != null) {
            player.getCooldowns().addCooldown(this, MAX_RECH);
        }
        target.level.playSound(null, player.blockPosition(), getCastSound(), SoundSource.AMBIENT);
    }
    @Override
    public void retarget(LivingEntity originalCaster) {
        BiConsumer<ServerPlayer, LivingEntity> ability = this::targetedAbility;
        ability.accept(null, originalCaster);
    }
}
