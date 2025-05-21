package net.arkadiyhimself.fantazia.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicMobEffectTagsProvider extends IntrinsicHolderTagsProvider<MobEffect> {
    public FantazicMobEffectTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.MOB_EFFECT, pLookupProvider, (mobEffect -> BuiltInRegistries.MOB_EFFECT.getResourceKey(mobEffect).get()), Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        // fantazia
        tag(FTZMobEffectTags.Cleanse.MEDIUM).add(MobEffects.DIG_SPEED.value(), MobEffects.DIG_SLOWDOWN.value(), MobEffects.FIRE_RESISTANCE.value(), MobEffects.WATER_BREATHING.value(), MobEffects.INVISIBILITY.value(), MobEffects.WITHER.value(), MobEffects.ABSORPTION.value(), MobEffects.GLOWING.value(), MobEffects.LUCK.value(), MobEffects.UNLUCK.value(), FTZMobEffects.BARRIER.value(), FTZMobEffects.LAYERED_BARRIER.value(), FTZMobEffects.REFLECT.value());
        tag(FTZMobEffectTags.Cleanse.POWERFUL).add(MobEffects.DAMAGE_RESISTANCE.value(), MobEffects.HEALTH_BOOST.value(), FTZMobEffects.FURY.value(), FTZMobEffects.STUN.value(), FTZMobEffects.DISARM.value());
        tag(FTZMobEffectTags.Cleanse.ABSOLUTE).add(MobEffects.BAD_OMEN.value(), MobEffects.HERO_OF_THE_VILLAGE.value(), FTZMobEffects.ABSOLUTE_BARRIER.value(), FTZMobEffects.DOOMED.value());

        tag(FTZMobEffectTags.BARRIER).add(FTZMobEffects.BARRIER.value(), FTZMobEffects.LAYERED_BARRIER.value(), FTZMobEffects.ABSOLUTE_BARRIER.value());
        tag(FTZMobEffectTags.INTERRUPT).add(FTZMobEffects.STUN.value(), FTZMobEffects.MICROSTUN.value());
        tag(FTZMobEffectTags.NO_PARTICLES).add(
                FTZMobEffects.ABSOLUTE_BARRIER.value(),
                FTZMobEffects.BARRIER.value(), FTZMobEffects.HAEMORRHAGE.value(),
                FTZMobEffects.FURY.value(),
                FTZMobEffects.STUN.value(),
                FTZMobEffects.LAYERED_BARRIER.value(),
                FTZMobEffects.DEAFENED.value(),
                FTZMobEffects.FROZEN.value(),
                FTZMobEffects.MIGHT.value(),
                FTZMobEffects.DOOMED.value(),
                FTZMobEffects.DISARM.value(),
                FTZMobEffects.REFLECT.value(),
                FTZMobEffects.DEFLECT.value(),
                FTZMobEffects.CURSED_MARK.value(),
                FTZMobEffects.ELECTROCUTED.value(),
                FTZMobEffects.DISGUISED.value(),
                FTZMobEffects.PUPPETEERED.value()
        );
    }
}
