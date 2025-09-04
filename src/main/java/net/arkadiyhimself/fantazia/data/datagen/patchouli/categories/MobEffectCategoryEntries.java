package net.arkadiyhimself.fantazia.data.datagen.patchouli.categories;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.TheWorldlinessEntryHelper;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.Categories;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntry;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntryHolder;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoPage;
import net.arkadiyhimself.fantazia.common.mob_effect.IPatchouliEntry;
import net.arkadiyhimself.fantazia.common.mob_effect.SimpleMobEffect;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Consumer;

public class MobEffectCategoryEntries implements SubProvider<PseudoEntryHolder> {

    private static final String BENEFICIAL = "book.fantazia.heading.mob_effects.beneficial";
    private static final String HARMFUL = "book.fantazia.heading.mob_effects.harmful";
    private static final String NEUTRAL = "book.fantazia.heading.mob_effects.neutral";

    public static MobEffectCategoryEntries create() {
        return new MobEffectCategoryEntries();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<PseudoEntryHolder> consumer) {
        for (DeferredHolder<MobEffect, ? extends MobEffect> holder : FTZMobEffects.REGISTER.getEntries())
            if (holder.value() instanceof SimpleMobEffect simpleMobEffect && simpleMobEffect.patchouliEntry() || holder.value() instanceof IPatchouliEntry) mobEffectEntry(consumer, holder);

    }

    private static void mobEffectEntry(Consumer<PseudoEntryHolder> consumer, DeferredHolder<MobEffect, ? extends MobEffect> holder) {
        String mobEffect = holder.getId().getPath();
        String basicString = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + ".mob_effects." + mobEffect + ".";
        ResourceLocation icon = Fantazia.location("textures/mob_effect/" + mobEffect + ".png");
        ResourceLocation advancement = Fantazia.location(TheWorldlinessEntryHelper.THE_WORLDLINESS + "/mob_effects/" + mobEffect);
        String title = switch (holder.value().getCategory()) {
            case BENEFICIAL -> BENEFICIAL;
            case HARMFUL -> HARMFUL;
            case NEUTRAL -> NEUTRAL;
        };

        PseudoEntry.Builder builder = PseudoEntry.builder().name(holder.value().getDescriptionId()).icon(icon).advancement(advancement).category(Categories.MOB_EFFECTS);

        builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicString + "page." + 1).title(title).build());
        builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicString + "page." + 2).build());

        builder.build().save(consumer, holder.getId().withPrefix(Categories.MOB_EFFECTS.getPath() + "/"));
    }
}
