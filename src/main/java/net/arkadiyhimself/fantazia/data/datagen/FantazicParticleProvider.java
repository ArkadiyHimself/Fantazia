package net.arkadiyhimself.fantazia.data.datagen;

import net.arkadiyhimself.fantazia.common.registries.FTZParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FantazicParticleProvider extends ParticleDescriptionProvider {

    public FantazicParticleProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        barrierFolder(FTZParticleTypes.BARRIER_PIECE1);
        barrierFolder(FTZParticleTypes.BARRIER_PIECE2);
        barrierFolder(FTZParticleTypes.BARRIER_PIECE3);
        barrierFolder(FTZParticleTypes.BARRIER_PIECE4);
        barrierFolder(FTZParticleTypes.BARRIER_PIECE5);
    }

    private void withFolder(DeferredHolder<ParticleType<?>, ?> holder, String folder) {
        sprite(holder.value(), holder.getId().withPrefix(folder + "/"));
    }

    private void barrierFolder(DeferredHolder<ParticleType<?>, SimpleParticleType> holder) {
        withFolder(holder,"barrier/");
    }

    private void barrierFuryFolder() {

    }
}
