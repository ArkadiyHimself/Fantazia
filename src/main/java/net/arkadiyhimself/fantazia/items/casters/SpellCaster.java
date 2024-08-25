package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.advanced.spell.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.Spell;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.advanced.spell.TargetedSpell;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.ManaData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SpellCaster extends Item {
    private final Supplier<Spell> spell;
    public SpellCaster(Supplier<Spell> spell) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.spell = spell;
    }
    public boolean tryCast(@NotNull ServerPlayer serverPlayer) {
        if (serverPlayer.getCooldowns().isOnCooldown(this) && !serverPlayer.getAbilities().instabuild || getSpell() == null) return false;

        ManaData manaData = AbilityGetter.takeAbilityHolder(serverPlayer, ManaData.class);
        if (manaData != null && !manaData.wasteMana(getSpell().getManacost())) return false;

        boolean flag = false;

        if (this.getSpell() instanceof SelfSpell selfSpell) flag = SpellHelper.trySelfSpell(serverPlayer, selfSpell, false);
        if (this.getSpell() instanceof TargetedSpell<?> targetedSpell) flag = SpellHelper.tryTargetedSpell(serverPlayer, targetedSpell);

        if (flag && !serverPlayer.getAbilities().instabuild) serverPlayer.getCooldowns().addCooldown(this, getSpell().getRecharge());
        return flag;
    }
    @Nullable
    public Spell getSpell() {
        return spell.get();
    }
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        if (getSpell() != null) components.addAll(getSpell().buildItemTooltip(null));
        return components;
    }
}
