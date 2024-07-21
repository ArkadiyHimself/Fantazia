package net.arkadiyhimself.fantazia.Items.casters;

import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.arkadiyhimself.fantazia.util.KeyBinding;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class DashStone extends Item implements ICurioItem, Vanishable {
    private final KeyMapping keyBind = KeyBinding.DASH;
    public final int level;
    public DashStone(int level) {
        super(getDefaultProperties());
        this.level = level;
    }
    public static Properties getDefaultProperties() {
        Properties props = new Properties();

        props.stacksTo(1);
        props.rarity(Rarity.RARE);
        props.fireResistant();

        return props;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return ICurioItem.super.canEquip(slotContext, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(" "));

        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.GOLD};
        ChatFormatting[] button = new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.RED};
        ChatFormatting[] ability = new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_RED};
        ChatFormatting[] list = new ChatFormatting[]{ChatFormatting.RED};

        int lines = 0;
        String basicPath = "dash.fantazia.dash" + level;

        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {}
            if (lines > 0) {
                for (int i = 1; i <= lines; i++) {
                    WhereMagicHappens.Gui.addComponent(pTooltipComponents, basicPath + ".desc." + i, null, null);
                }
            }
            return;
        }
        // dash name
        WhereMagicHappens.Gui.addComponent(pTooltipComponents, "tooltip.fantazia.common.active", list, ability, Component.translatable(basicPath + ".name").getString());

        String desc = Component.translatable(basicPath + ".lines").getString();
        try {
            lines = Integer.parseInt(desc);
        } catch (NumberFormatException ignored) {}
        if (lines > 0) {
            pTooltipComponents.add(Component.translatable(" "));
            for (int i = 1; i <= lines; i++) {
                WhereMagicHappens.Gui.addComponent(pTooltipComponents, basicPath + "." + i, text, null);
            }
        }

        String stats = Component.translatable(basicPath + ".stats.lines").getString();
        lines = 0;
        try {
            lines = Integer.parseInt(stats);
        } catch (NumberFormatException ignored) {}
        if (lines > 0) {
            pTooltipComponents.add(Component.translatable(" "));
            for (int i = 1; i <= lines; i++) {
                WhereMagicHappens.Gui.addComponent(pTooltipComponents, basicPath + ".stats." + i, null, null);
            }
        }
    }
}
