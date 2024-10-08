package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.advanced.spell.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.advanced.spell.TargetedSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.ManaHolder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellCasterItem extends Item {
    private final Holder<AbstractSpell> spell;
    public SpellCasterItem(Holder<AbstractSpell> spell) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.spell = spell;
    }
    public boolean tryCast(@NotNull ServerPlayer serverPlayer) {
        if (serverPlayer.getCooldowns().isOnCooldown(this) && !serverPlayer.getAbilities().instabuild || getSpell() == null) return false;

        ManaHolder manaHolder = PlayerAbilityGetter.takeHolder(serverPlayer, ManaHolder.class);
        if (manaHolder != null && !manaHolder.wasteMana(getSpell().getManacost())) return false;

        boolean flag = false;

        if (this.getSpell() instanceof SelfSpell selfSpell) flag = SpellHelper.trySelfSpell(serverPlayer, selfSpell, false);
        if (this.getSpell() instanceof TargetedSpell<?> targetedSpell) flag = SpellHelper.tryTargetedSpell(serverPlayer, targetedSpell);

        if (flag && !serverPlayer.getAbilities().instabuild) serverPlayer.getCooldowns().addCooldown(this, getSpell().getRecharge());
        return flag;
    }
    @Nullable
    public AbstractSpell getSpell() {
        return spell.value();
    }
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        if (getSpell() != null) components.addAll(getSpell().itemTooltip(null));
        return components;
    }
}
