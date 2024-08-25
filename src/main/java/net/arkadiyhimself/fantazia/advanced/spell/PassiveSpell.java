package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class PassiveSpell extends Spell {
    public PassiveSpell(float MANACOST, int recharge, Supplier<SoundEvent> castSound) {
        super(MANACOST, recharge, castSound);
    }
    public PassiveSpell(float MANACOST, int recharge) {
        super(MANACOST, recharge, null);
    }

    @Override
    public PassiveSpell cleanse(Cleanse cleanse) {
        super.cleanse(cleanse);
        return this;
    }

    @Override
    public List<Component> buildItemTooltip(@Nullable ItemStack itemStack) {
        List<Component> components = Lists.newArrayList();
        if (getID() == null) return components;
        String basicPath = "ability." + this.getID().getNamespace() + "." + this.getID().getPath();
        int lines = 0;
        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                components.add(Component.translatable(" "));
                for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".desc." + i, null, null));
            }
            return components;
        }
        components.add(Component.translatable(" "));
        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};

        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD};
        ChatFormatting[] head = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        // spell name
        String namePath = basicPath + ".name";
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.passive", head, ability, Component.translatable(namePath).getString()));
        // spell recharge
        String recharge = String.format("%.1f", ((float) this.getRecharge()) / 20);
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.recharge", head, ability, recharge));
        // spell manacost
        String manacost = String.format("%.1f", this.getManacost());
        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.manacost", head, ability, manacost));
        // spell cleanse
        if (this.hasCleanse()) components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.cleanse_strength", head, ability, this.getStrength().getName()));

        components.add(Component.translatable(" "));


        String desc = Component.translatable(basicPath + ".lines").getString();
        try {
            lines = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, text, null));

        String pass = Component.translatable(basicPath + ".tweaks.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(pass);
        } catch (NumberFormatException ignored) {}

        if (lines > 0) {
            components.add(Component.translatable(" "));
            components.add(GuiHelper.bakeComponent(basicPath + ".tweaks", head, null));
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".tweaks." + i, null, null));
        }

        return components;
    }
}
