package net.arkadiyhimself.fantazia.networking.commands;

import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface CommandsHandlers {

    static void buildAuraTooltip(ResourceLocation id) {
        Aura aura = FantazicRegistries.AURAS.get(id);
        LocalPlayer player = Minecraft.getInstance().player;
        if (aura == null || player == null) return;

        for (Component component : aura.buildTooltip()) player.sendSystemMessage(component);
    }

    static void buildRuneTooltip(ResourceLocation id) {
        Rune rune = FantazicRegistries.RUNES.get(id);
        LocalPlayer player = Minecraft.getInstance().player;
        if (rune == null || player == null) return;

        for (Component component : rune.buildTooltip()) player.sendSystemMessage(component);
    }

    static void buildSpellTooltip(ResourceLocation id) {
        AbstractSpell spell = FantazicRegistries.SPELLS.get(id);
        LocalPlayer player = Minecraft.getInstance().player;
        if (spell == null || player == null) return;

        for (Component component : spell.buildTooltip()) player.sendSystemMessage(component);
    }
}
