package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.SpellInstancesHolder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpellCasterItem extends Item {

    private final Holder<AbstractSpell> spell;

    public SpellCasterItem(Holder<AbstractSpell> spell) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.spell = spell;
    }

    public boolean tryCast(@NotNull ServerPlayer serverPlayer) {
        SpellInstancesHolder spellInstancesHolder = PlayerAbilityHelper.takeHolder(serverPlayer, SpellInstancesHolder.class);
        return spellInstancesHolder != null && spellInstancesHolder.tryToUse(getSpell());
    }

    public Holder<AbstractSpell> getSpell() {
        return spell;
    }

    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        if (spell.value() instanceof SelfSpell) components.addAll(SelfSpell.itemTooltip(spell));
        else components.addAll(getSpell().value().itemTooltip(null));

        return components;
    }
}
