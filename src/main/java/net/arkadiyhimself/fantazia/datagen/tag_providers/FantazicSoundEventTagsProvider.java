package net.arkadiyhimself.fantazia.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZSoundEventTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicSoundEventTagsProvider extends IntrinsicHolderTagsProvider<SoundEvent> {

    public FantazicSoundEventTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.SOUND_EVENT, lookupProvider, (soundEvent -> BuiltInRegistries.SOUND_EVENT.getResourceKey(soundEvent).get()), Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(FTZSoundEventTags.NOT_MUTED).add(SoundEvents.GENERIC_EXPLODE.value(), SoundEvents.WARDEN_SONIC_BOOM, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, FTZSoundEvents.HEART_BEAT1.value(), FTZSoundEvents.HEART_BEAT2.get(), FTZSoundEvents.DASH1_RECHARGE.value(), FTZSoundEvents.DASH2_RECHARGE.get(), FTZSoundEvents.DASH3_RECHARGE.value(), FTZSoundEvents.RINGING.get(), FTZSoundEvents.FURY_DISPEL.get(), FTZSoundEvents.FURY_PROLONG.get(), FTZSoundEvents.DAMNED_WRATH.get(), FTZSoundEvents.DOOMED.get(), FTZSoundEvents.UNDOOMED.get(), FTZSoundEvents.DENIED.get());
    }
}
