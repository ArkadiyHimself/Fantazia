package net.arkadiyhimself.fantazia.data.datagen.model;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.item.EngineeringTableBlock;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class FantazicBlockModelProvider extends BlockModelProvider {

    private static final ResourceLocation OBSCURE_PLANKS_TEXTURE = Fantazia.location("block/obscure_planks");

    public FantazicBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        magicBench(FTZBlocks.AMPLIFICATION_BENCH);
        sapling(FTZBlocks.OBSCURE_SAPLING);
        wood(FTZBlocks.OBSCURE_WOOD, FTZBlocks.OBSCURE_LOG);
        wood(FTZBlocks.STRIPPED_OBSCURE_WOOD, FTZBlocks.STRIPPED_OBSCURE_LOG);
        pottedSapling(FTZBlocks.POTTED_OBSCURE_SAPLING, FTZBlocks.OBSCURE_SAPLING);
        fullFireModels(FTZBlocks.ANCIENT_FLAME);

        leaves(FTZBlocks.OBSCURE_LEAVES.getId().toString(), Fantazia.location("block/obscure_leaves"));
        sign(FTZBlocks.OBSCURE_SIGN.getId().toString(), OBSCURE_PLANKS_TEXTURE);
        simpleSlab(FTZBlocks.OBSCURE_SLAB, OBSCURE_PLANKS_TEXTURE);

        // separate inventory model
        fenceInventory(FTZBlocks.OBSCURE_FENCE.getId().withSuffix("_inventory").toString(), OBSCURE_PLANKS_TEXTURE);
        buttonInventory(FTZBlocks.OBSCURE_BUTTON.getId().withSuffix("_inventory").toString(), OBSCURE_PLANKS_TEXTURE);

        // engineering tables
        engineeringTable(FTZBlocks.OAK_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("oak"));
        engineeringTable(FTZBlocks.SPRUCE_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("spruce"));
        engineeringTable(FTZBlocks.BIRCH_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("birch"));
        engineeringTable(FTZBlocks.JUNGLE_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("jungle"));
        engineeringTable(FTZBlocks.ACACIA_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("acacia"));
        engineeringTable(FTZBlocks.CHERRY_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("cherry"));
        engineeringTable(FTZBlocks.DARK_OAK_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("dark_oak"));
        engineeringTable(FTZBlocks.MANGROVE_ENGINEERING_TABLE, ResourceLocation.withDefaultNamespace("mangrove"));
        engineeringTable(FTZBlocks.BAMBOO_ENGINEERING_TABLE, Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_PLANKS);
        engineeringTable(FTZBlocks.CRIMSON_ENGINEERING_TABLE, Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_PLANKS);
        engineeringTable(FTZBlocks.WARPED_ENGINEERING_TABLE, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_PLANKS);
        engineeringTable(FTZBlocks.OBSCURE_ENGINEERING_TABLE, Fantazia.location("obscure"));
    }

    public void wood(DeferredBlock<? extends RotatedPillarBlock> block, DeferredBlock<? extends RotatedPillarBlock> log) {
        cubeColumn(block.getId().toString(), log.getId().withPrefix("block/"), log.getId().withPrefix("block/"));
    }

    public void simpleSlab(DeferredBlock<? extends SlabBlock> block, ResourceLocation texture) {
        slab(block.getId().toString(), texture, texture, texture);
        slabTop(block.getId().withSuffix("_top").toString(), texture, texture, texture);
    }

    public void sapling(DeferredBlock<? extends SaplingBlock> block) {
        this.singleTexture(block.getId().toString(), mcLoc("block/cross"), "cross", block.getId().withPrefix("block/")).renderType(RenderType.CUTOUT.name);
    }

    public void pottedSapling(DeferredBlock<? extends FlowerPotBlock> block, DeferredBlock<? extends SaplingBlock> sapling) {
        this.singleTexture(block.getId().toString(), mcLoc("block/flower_pot_cross"), "plant", sapling.getId().withPrefix("block/")).renderType(RenderType.CUTOUT.name);
    }

    private void magicBench(DeferredBlock<? extends Block> block) {
        ResourceLocation parent = Fantazia.location("block/template_amplification_bench");
        ResourceLocation base = block.getId().withPrefix("block/");

        getBuilder(base.toString())
                .parent(modelFile(parent))
                .texture("bottom", base.withSuffix("_bottom"))
                .texture("top", base.withSuffix("_top"))
                .texture("side", base.withSuffix("_side"));
    }

    private void engineeringTable(DeferredBlock<EngineeringTableBlock> block, Block log, Block strippedLog, Block planks) {
        ResourceLocation logTexture = BuiltInRegistries.BLOCK.getKey(log).withPrefix("block/");
        ResourceLocation strippedLogTexture = BuiltInRegistries.BLOCK.getKey(strippedLog).withPrefix("block/");
        ResourceLocation planksTexture = BuiltInRegistries.BLOCK.getKey(planks).withPrefix("block/");

        getBuilder(block.getId().toString())
                .parent(modelFile(Fantazia.location("block/template_engineering_table_base")))
                .texture("log", logTexture)
                .texture("stripped_log", strippedLogTexture)
                .texture("planks", planksTexture);

        getBuilder(block.getId().withPrefix("block/").withSuffix("_board").toString())
                .parent(modelFile(Fantazia.location("block/template_engineering_table_board")))
                .texture("log", logTexture)
                .texture("planks", planksTexture);
    }

    private void engineeringTable(DeferredBlock<EngineeringTableBlock> block, ResourceLocation base) {
        ResourceLocation logTexture = base.withSuffix("_log").withPrefix("block/");
        ResourceLocation strippedLogTexture = base.withPrefix("block/stripped_").withSuffix("_log");
        ResourceLocation planksTexture = base.withSuffix("_planks").withPrefix("block/");

        getBuilder(block.getId().toString())
                .parent(modelFile(Fantazia.location("block/template_engineering_table_base")))
                //.renderType(RenderType.CUTOUT.name)
                .texture("log", logTexture)
                .texture("stripped_log", strippedLogTexture)
                .texture("planks", planksTexture);

        getBuilder(block.getId().withPrefix("block/").withSuffix("_board").toString())
                .parent(modelFile(Fantazia.location("block/template_engineering_table_board")))
                //.renderType(RenderType.CUTOUT.name)
                .texture("log", logTexture)
                .texture("planks", planksTexture);
    }

    private void fullFireModels(DeferredBlock<? extends BaseFireBlock> block) {
        createFloorFireModels(block);
        createSideFireModels(block);
        createTopFireModels(block);
    }

    private void createFloorFireModels(DeferredBlock<? extends BaseFireBlock> block) {
        ResourceLocation baseId = block.getId().withPrefix("block/");
        ModelFile parent = modelFile(mcLoc("block/template_fire_floor"));

        getBuilder(baseId.withSuffix("_floor0").toString())
                .parent(parent)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_0"));
        getBuilder(baseId.withSuffix("_floor1").toString())
                .parent(parent)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_1"));
    }

    private void createSideFireModels(DeferredBlock<? extends BaseFireBlock> block) {
        ResourceLocation baseId = block.getId().withPrefix("block/");

        ModelFile side = modelFile(mcLoc("block/template_fire_side"));
        ModelFile side_alt = modelFile(mcLoc("block/template_fire_side_alt"));

        getBuilder(baseId.withSuffix("_side0").toString())
                .parent(side)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_0"));
        getBuilder(baseId.withSuffix("_side1").toString())
                .parent(side)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_1"));

        getBuilder(baseId.withSuffix("_side_alt0").toString())
                .parent(side_alt)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_0"));
        getBuilder(baseId.withSuffix("_side_alt1").toString())
                .parent(side_alt)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_1"));
    }

    private void createTopFireModels(DeferredBlock<? extends BaseFireBlock> block) {
        ResourceLocation baseId = block.getId().withPrefix("block/");

        ModelFile up = modelFile(mcLoc("block/template_fire_up"));
        ModelFile up_alt = modelFile(mcLoc("block/template_fire_up_alt"));

        getBuilder(baseId.withSuffix("_up0").toString())
                .parent(up)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_0"));
        getBuilder(baseId.withSuffix("_up1").toString())
                .parent(up)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_1"));

        getBuilder(baseId.withSuffix("_up_alt0").toString())
                .parent(up_alt)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_0"));
        getBuilder(baseId.withSuffix("_up_alt1").toString())
                .parent(up_alt)
                .renderType(RenderType.CUTOUT.name)
                .texture("fire", baseId.withSuffix("_1"));
    }

    private ModelFile.ExistingModelFile modelFile(ResourceLocation location) {
        return new ModelFile.ExistingModelFile(location, existingFileHelper);
    }
}
