package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MobEffectTagsProvider extends IntrinsicHolderTagsProvider<MobEffect> {
    public MobEffectTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.MOB_EFFECT, pLookupProvider, (mobEffect -> ForgeRegistries.MOB_EFFECTS.getResourceKey(mobEffect).get()), Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(FTZMobEffectTags.BARRIER).add(FTZMobEffects.BARRIER, FTZMobEffects.LAYERED_BARRIER, FTZMobEffects.ABSOLUTE_BARRIER);
        this.tag(FTZMobEffectTags.INTERRUPT).add(FTZMobEffects.STUN, FTZMobEffects.MICROSTUN);
    }
}
