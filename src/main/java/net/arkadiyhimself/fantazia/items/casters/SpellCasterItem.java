package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.advanced.spell.SpellCastResult;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.SpellInstancesHolder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public SpellCastResult tryCast(@NotNull ServerPlayer serverPlayer) {
        SpellInstancesHolder spellInstancesHolder = PlayerAbilityHelper.takeHolder(serverPlayer, SpellInstancesHolder.class);
        return spellInstancesHolder == null ? SpellCastResult.fail() : spellInstancesHolder.tryToUse(getSpell());
    }

    public Holder<AbstractSpell> getSpell() {
        return spell;
    }

    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        if (!Screen.hasShiftDown()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(this);
            String basicPath = "item." + id.getNamespace() + "." + id.getPath();
            int lines = 0;
            String desc = Component.translatable(basicPath + ".lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                components.add(Component.literal(" "));
                for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, null, null));
            }
        } else components.addAll(getSpell().value().buildTooltip());


        return components;
    }
}
