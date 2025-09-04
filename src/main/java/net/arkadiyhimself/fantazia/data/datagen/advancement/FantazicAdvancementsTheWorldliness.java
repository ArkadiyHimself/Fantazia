package net.arkadiyhimself.fantazia.data.datagen.advancement;

import net.arkadiyhimself.fantazia.data.datagen.advancement.the_worldliness.*;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FantazicAdvancementsTheWorldliness implements AdvancementProvider.AdvancementGenerator {

    public static FantazicAdvancementsTheWorldliness create() {
        return new FantazicAdvancementsTheWorldliness();
    }

    @Override
    public void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        ArtifactCategoryAdvancements.generate(provider, consumer, existingFileHelper);
        EnchantmentCategoryAdvancements.generate(provider, consumer, existingFileHelper);
        ExpendableCategoryAdvancements.generate(provider, consumer, existingFileHelper);
        MobEffectCategoryAdvancements.generate(provider, consumer, existingFileHelper);
        WeaponCategoryAdvancements.generate(provider, consumer, existingFileHelper);
        WorldCategoryAdvancements.generate(provider, consumer, existingFileHelper);
    }
}
