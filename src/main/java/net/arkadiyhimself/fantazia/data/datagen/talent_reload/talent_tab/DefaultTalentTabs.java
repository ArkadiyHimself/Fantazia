package net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent_tab;

import net.arkadiyhimself.fantazia.client.screen.TalentTab;
import net.arkadiyhimself.fantazia.data.talent.TalentTabs;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;

public class DefaultTalentTabs implements SubProvider<TalentTabBuilderHolder> {

    public static DefaultTalentTabs create() {
        return new DefaultTalentTabs();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<TalentTabBuilderHolder> consumer) {
        create(consumer, TalentTabs.ABILITIES, false);
        create(consumer, TalentTabs.STAT_MODIFIERS, false);
        create(consumer, TalentTabs.SPELLCASTING, true);
    }

    private void create(Consumer<TalentTabBuilderHolder> consumer, ResourceLocation id, boolean customBackground) {
        ResourceLocation icon = id.withPrefix("textures/gui/talent_tabs/").withSuffix("/icon.png");
        String title = Util.makeDescriptionId("talent_tab", id);
        ResourceLocation background = customBackground ? id.withPrefix("textures/gui/talent_tabs/").withSuffix("/background.png") : null;
        new TalentTab.Builder(icon, title, Optional.ofNullable(background)).save(consumer, id);
    }
}
