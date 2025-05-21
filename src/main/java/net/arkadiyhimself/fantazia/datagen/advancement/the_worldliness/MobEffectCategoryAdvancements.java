package net.arkadiyhimself.fantazia.datagen.advancement.the_worldliness;

import net.arkadiyhimself.fantazia.mob_effect.IPatchouliEntry;
import net.arkadiyhimself.fantazia.mob_effect.SimpleMobEffect;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class MobEffectCategoryAdvancements {

    public static void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        for (DeferredHolder<MobEffect, ? extends MobEffect> reference : FTZMobEffects.REGISTER.getEntries()) mobEffect(consumer, reference);
    }

    private static void mobEffect(Consumer<AdvancementHolder> consumer, DeferredHolder<MobEffect, ? extends MobEffect> mobEffect) {
        if (mobEffect.get() instanceof SimpleMobEffect effect && !effect.patchouliEntry() && !(effect instanceof IPatchouliEntry)) return;
        EntryAdvancementHolder holder = new EntryAdvancementHolder("mob_effects");

        ResourceLocation location = mobEffect.getId();
        holder.addCriterion(location.getPath(),
                EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(mobEffect)))
                .save(consumer, location);
    }
}
