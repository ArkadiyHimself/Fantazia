package net.arkadiyhimself.fantazia.client.screen;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public record AmplificationTab(
        Holder<Item> item,
        String ident,
        ResourceLocation screen,
        ResourceLocation[] tabs,
        ChatFormatting[] nameFormat,
        ChatFormatting[] textFormat,
        ChatFormatting[] titleFormat
) implements ITooltipBuilder {

    public static final Codec<AmplificationTab> CODEC = Codec.STRING.xmap(AmplificationTab::getTab, AmplificationTab::ident);
    public static final StreamCodec<ByteBuf, AmplificationTab> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(AmplificationTab::getTab, AmplificationTab::ident);
    public static final List<AmplificationTab> TABS = Lists.newArrayList();

    public static final AmplificationTab RUNE_CARVING;
    public static final AmplificationTab AMPLIFICATION;
    public static final AmplificationTab ENCHANTMENT_REPLACE;

    public static AmplificationTab getTab(String name) {
        for (AmplificationTab tab : TABS) if (tab.ident.equals(name)) return tab;
        return RUNE_CARVING;
    }

    public String ident() {
        return ident;
    }

    public ItemStack stack() {
        return item.value().getDefaultInstance();
    }

    public String descId() {
        return "container." + Fantazia.MODID + ".amplification." + ident;
    }

    public Component titleComponent() {
        return Component.translatable(descId() + ".title").withStyle(titleFormat);
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        String path = descId();

        if (!Screen.hasShiftDown()) {
            Component name = Component.translatable(path).withStyle(nameFormat);
            components.add(name);
            return components;
        }

        Component lines = Component.translatable(path + ".lines");
        int l = 0;
        try {
            l = Integer.parseInt(lines.getString());
        } catch (NumberFormatException ignored) {}
        if (l > 0) for (int i = 1; i <= l; i++) {
            components.add(Component.translatable(path + "." + i).withStyle(textFormat));
        }

        return components;
    }

    public ResourceLocation getSelectedTab() {
        return tabs[0];
    }

    public ResourceLocation getSelectableTab() {
        return tabs[1];
    }

    public ResourceLocation getUnselectedTab() {
        return tabs[2];
    }

    public static Builder builder(Holder<Item> item, String ident) {
        return new Builder(item, ident);
    }

    public static class Builder {

        private final Holder<Item> item;
        private final String ident;

        private ChatFormatting[] nameFormat = new ChatFormatting[]{};
        private ChatFormatting[] textFormat = new ChatFormatting[]{};
        private ChatFormatting[] titleFormat = new ChatFormatting[]{};

        private Builder(Holder<Item> item, String ident) {
            this.item = item;
            this.ident = ident;
        }

        public Builder nameFormat(ChatFormatting... nameFormat) {
            this.nameFormat = nameFormat;
            return this;
        }

        public Builder textFormat(ChatFormatting... textFormat) {
            this.textFormat = textFormat;
            return this;
        }

        public Builder titleFormat(ChatFormatting... titleFormat) {
            this.titleFormat = titleFormat;
            return this;
        }

        public AmplificationTab build() {
            ResourceLocation screen = Fantazia.location("textures/gui/container/amplification_bench/" + ident + ".png");
            ResourceLocation[] tabs = new ResourceLocation[3];
            tabs[0] = Fantazia.location("container/amplification_bench/" + ident + "/selected_tab");
            tabs[1] = Fantazia.location("container/amplification_bench/" + ident + "/selectable_tab");
            tabs[2] = Fantazia.location("container/amplification_bench/" + ident + "/unselected_tab");
            AmplificationTab tab = new AmplificationTab(
                    item,
                    ident,
                    screen,
                    tabs,
                    nameFormat,
                    textFormat,
                    titleFormat
            );
            TABS.add(tab);
            return tab;
        }
    }

    static {
        RUNE_CARVING = builder(FTZItems.RUNE_WIELDER, "rune_carving")
                .nameFormat(ChatFormatting.BOLD, ChatFormatting.GRAY)
                .titleFormat(ChatFormatting.DARK_BLUE).textFormat(ChatFormatting.DARK_GRAY)
                .build();

        AMPLIFICATION = builder(FTZItems.AMPLIFIER, "amplification")
                .nameFormat(ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE)
                .textFormat(ChatFormatting.LIGHT_PURPLE).titleFormat(ChatFormatting.DARK_PURPLE)
                .build();

        ENCHANTMENT_REPLACE = builder(Holder.direct(Items.ENCHANTED_BOOK), "enchantment_replace")
                .nameFormat(ChatFormatting.BOLD, ChatFormatting.GOLD)
                .textFormat(ChatFormatting.YELLOW).titleFormat(ChatFormatting.RED)
                .build();
    }
}
