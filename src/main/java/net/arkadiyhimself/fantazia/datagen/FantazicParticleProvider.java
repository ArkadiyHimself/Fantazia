package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.common.registries.FTZParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class FantazicParticleProvider extends ParticleDescriptionProvider {

    public FantazicParticleProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        framesVanilla(FTZParticleTypes.CHAINED, ResourceLocation.withDefaultNamespace("generic_"), 8);
        variants(FTZParticleTypes.BARRIER_PIECE,5);
        variants(FTZParticleTypes.BARRIER_PIECE_FURY,5);
        variants(FTZParticleTypes.BLOOD,5);
        frames(FTZParticleTypes.DOOMED_SOUL1,9);
        frames(FTZParticleTypes.DOOMED_SOUL2,9);
        frames(FTZParticleTypes.DOOMED_SOUL3,9);
        variants(FTZParticleTypes.ELECTRO,5);
        frames(FTZParticleTypes.FALLEN_SOUL,12);
        variants(FTZParticleTypes.LIFESTEAL,5);
        variants(FTZParticleTypes.METAL_SCRAP,4);
        variants(FTZParticleTypes.REGEN,3);
        frames(FTZParticleTypes.TIME_TRAVEL,8);
        frames(FTZParticleTypes.WITHER,5);
    }

    private void variants(DeferredHolder<ParticleType<?>, ?> holder, int amount) {
        List<ResourceLocation> textures = Lists.newArrayList();
        for (int i = 1; i <= amount; i++)
            textures.add(holder.getId().withSuffix("/variant" + i));
        spriteSet(holder.value(), textures);
    }

    private void frames(DeferredHolder<ParticleType<?>, ?> holder, int amount) {
        List<ResourceLocation> textures = Lists.newArrayList();
        for (int i = 1; i <= amount; i++)
            textures.add(holder.getId().withSuffix("/frame" + i));
        spriteSet(holder.value(), textures);
    }

    private void framesVanilla(DeferredHolder<ParticleType<?>, ?> holder, ResourceLocation texture, int amount) {
        List<ResourceLocation> textures = Lists.newArrayList();
        for (int i = 0; i < amount; i++)
            textures.add(texture.withSuffix(String.valueOf(i)));
        spriteSet(holder.value(), textures);
    }
}
