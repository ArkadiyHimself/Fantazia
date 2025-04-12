package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class FantazicBlockStateProvider extends BlockStateProvider {

    public FantazicBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Fantazia.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(FTZBlocks.FANTAZIUM_ORE);
        blockWithItem(FTZBlocks.DEEPSLATE_FANTAZIUM_ORE);
        blockWithItem(FTZBlocks.FANTAZIUM_BLOCK);
        blockWithItem(FTZBlocks.RAW_FANTAZIUM_BLOCK);
    }

    private void simpleBlockAndItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(Fantazia.MODID + ":block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(Fantazia.MODID + ":block/" + deferredBlock.getId().getPath() + appendix));
    }
}
