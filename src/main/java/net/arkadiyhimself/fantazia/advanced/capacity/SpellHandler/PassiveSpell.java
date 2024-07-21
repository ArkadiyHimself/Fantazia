package net.arkadiyhimself.fantazia.advanced.capacity.SpellHandler;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class PassiveSpell extends Spell {
    public PassiveSpell(float MANACOST, int recharge, ResourceLocation id, @Nullable SoundEvent castSound) {
        super(MANACOST, recharge, id, castSound);
    }
    public PassiveSpell(float MANACOST, int recharge, ResourceLocation id) {
        super(MANACOST, recharge, id, null);
        SpellHelper.PASSIVE_SPELLS.put(this.getId(), this);
    }

    @Override
    public PassiveSpell cleanse(Cleanse cleanse) {
        super.cleanse(cleanse);
        return this;
    }

    @Override
    public List<Component> buildTooltip(@Nullable ItemStack itemStack) {
        List<Component> components = Lists.newArrayList();
        String basicPath = "ability." + this.getId().getNamespace() + "." + this.getId().getPath();
        int lines = 0;
        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                components.add(Component.translatable(" "));
                for (int i = 1; i <= lines; i++) {
                    WhereMagicHappens.Gui.addComponent(components, basicPath + ".desc." + i, null, null);
                }
            }
            return components;
        }
        components.add(Component.translatable(" "));
        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};

        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
        ChatFormatting[] head = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        // spell name
        String namePath = basicPath + ".name";
        WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.common.passive", head, ability, Component.translatable(namePath).getString());
        // spell recharge
        String recharge = String.format("%.1f", ((float) this.getRecharge()) / 20);
        WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.common.recharge", head, ability, recharge);
        // spell manacost
        String manacost = String.format("%.1f", this.getManacost());
        WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.common.manacost", head, ability, manacost);
        // spell cleanse
        if (this.hasCleanse()) WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.common.cleanse_strength", head, ability, this.getStrength().getName());

        components.add(Component.translatable(" "));


        String desc = Component.translatable(basicPath + ".lines").getString();
        try {
            lines = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            for (int i = 1; i <= lines; i++) {
                WhereMagicHappens.Gui.addComponent(components, basicPath + "." + i, text, null);
            }
        }

        String pass = Component.translatable(basicPath + ".tweaks.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(pass);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            components.add(Component.translatable(" "));
            WhereMagicHappens.Gui.addComponent(components, basicPath + ".tweaks", head, null);
            for (int i = 1; i <= lines; i++) {
                WhereMagicHappens.Gui.addComponent(components, basicPath + ".tweaks." + i, null, null);
            }
        }

        return components;
    }
}
