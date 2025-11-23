package net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent_tab;

import net.arkadiyhimself.fantazia.client.screen.TalentTab;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
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
        create(consumer,
                TalentTabs.ABILITIES,
                List.of(ChatFormatting.BOLD, ChatFormatting.DARK_PURPLE),
                List.of(ChatFormatting.LIGHT_PURPLE),
                false
        );

        create(consumer,
                TalentTabs.STAT_MODIFIERS,
                List.of(ChatFormatting.BOLD, ChatFormatting.DARK_BLUE),
                List.of(ChatFormatting.BLUE),
                false
        );

        create(consumer,
                TalentTabs.SPELLCASTING,
                List.of(ChatFormatting.BOLD, ChatFormatting.DARK_BLUE),
                List.of(ChatFormatting.BLUE),
                true
        );

        create(consumer,
                TalentTabs.ENGINEERING,
                List.of(ChatFormatting.BOLD, ChatFormatting.GRAY),
                List.of(ChatFormatting.WHITE),
                true);
    }

    private void create(
            Consumer<TalentTabBuilderHolder> consumer,
            ResourceLocation id,
            List<ChatFormatting> title,
            List<ChatFormatting> desc,
            boolean customBackground
    ) {
        ResourceLocation icon = id.withPrefix("textures/gui/talent_tabs/").withSuffix("/icon.png");
        String ident = Util.makeDescriptionId("talent_tab", id);
        ResourceLocation background = customBackground ? id.withPrefix("textures/gui/talent_tabs/").withSuffix("/background.png") : null;
        new TalentTab.Builder(
                icon,
                ident,
                title,
                desc,
                Optional.ofNullable(background)
        ).save(consumer, id);
    }
}
