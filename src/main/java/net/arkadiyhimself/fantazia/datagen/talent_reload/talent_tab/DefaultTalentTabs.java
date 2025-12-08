package net.arkadiyhimself.fantazia.datagen.talent_reload.talent_tab;

import net.arkadiyhimself.fantazia.client.screen.TalentTab;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.talent.TalentTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DefaultTalentTabs implements SubProvider<TalentTabBuilderHolder> {

    public static DefaultTalentTabs create() {
        return new DefaultTalentTabs();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<TalentTabBuilderHolder> consumer) {
        defaultTab(consumer,
                TalentTabs.ABILITIES,
                List.of(ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE),
                List.of(ChatFormatting.LIGHT_PURPLE)
        );

        defaultTab(consumer,
                TalentTabs.STAT_MODIFIERS,
                List.of(ChatFormatting.BOLD, ChatFormatting.DARK_BLUE),
                List.of(ChatFormatting.BLUE)
        );

        withCustomBackground(consumer,
                TalentTabs.SPELLCASTING,
                List.of(ChatFormatting.BOLD, ChatFormatting.DARK_BLUE),
                List.of(ChatFormatting.BLUE),
                512,512
        );

        withCustomBackground(consumer,
                TalentTabs.ENGINEERING,
                List.of(ChatFormatting.BOLD, ChatFormatting.GRAY),
                List.of(ChatFormatting.WHITE),
                160,160
        );
    }

    private void withCustomBackground(
            Consumer<TalentTabBuilderHolder> consumer,
            ResourceLocation id,
            List<ChatFormatting> title,
            List<ChatFormatting> desc,
            int bgWidth,
            int bgHeight
    ) {
        ResourceLocation icon = id.withPrefix("textures/gui/talent_tabs/").withSuffix("/icon.png");
        String ident = Util.makeDescriptionId("talent_tab", id);
        ResourceLocation background = id.withPrefix("textures/gui/talent_tabs/").withSuffix("/background.png");
        new TalentTab.Builder(
                icon,
                ident,
                title,
                desc,
                Optional.of(background),
                bgWidth,
                bgHeight
        ).save(consumer, id);
    }

    private void defaultTab(
            Consumer<TalentTabBuilderHolder> consumer,
            ResourceLocation id,
            List<ChatFormatting> title,
            List<ChatFormatting> desc
    ) {
        ResourceLocation icon = id.withPrefix("textures/gui/talent_tabs/").withSuffix("/icon.png");
        String ident = Util.makeDescriptionId("talent_tab", id);
        new TalentTab.Builder(
                icon,
                ident,
                title,
                desc,
                Optional.empty(),
                512,
                512
        ).save(consumer, id);
    }
}
