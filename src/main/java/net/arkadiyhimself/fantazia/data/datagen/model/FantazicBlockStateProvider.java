package net.arkadiyhimself.fantazia.data.datagen.model;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.blocks.AncientFlameBlock;
import net.arkadiyhimself.fantazia.common.item.EngineeringTableBlock;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class FantazicBlockStateProvider extends BlockStateProvider {

    private static final ResourceLocation OBSCURE_PLANKS = Fantazia.location("block/obscure_planks");
    private final ExistingFileHelper helper;

    public FantazicBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Fantazia.MODID, exFileHelper);
        this.helper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        createCustomFullFire(FTZBlocks.ANCIENT_FLAME);
        simpleBlockItemWithNoBlockModel(FTZBlocks.AMPLIFICATION_BENCH);
        blockWithItem(FTZBlocks.DEEPSLATE_FANTAZIUM_ORE);
        blockWithItem(FTZBlocks.FANTAZIUM_BLOCK);
        blockWithItem(FTZBlocks.FANTAZIUM_ORE);
        buttonBlock(FTZBlocks.OBSCURE_BUTTON, OBSCURE_PLANKS);
        doorBlock(FTZBlocks.OBSCURE_DOOR);
        fenceBlock(FTZBlocks.OBSCURE_FENCE, OBSCURE_PLANKS);
        fenceGateBlock(FTZBlocks.OBSCURE_FENCE_GATE, OBSCURE_PLANKS);
        hangingSignBlock(FTZBlocks.OBSCURE_HANGING_SIGN.value(), FTZBlocks.OBSCURE_WALL_HANGING_SIGN.value(), OBSCURE_PLANKS);
        blockWithItem(FTZBlocks.OBSCURE_LEAVES);
        logBlock(FTZBlocks.OBSCURE_LOG);
        simpleBlockAndItem(FTZBlocks.OBSCURE_PLANKS);
        pressurePlateBlock(FTZBlocks.OBSCURE_PRESSURE_PLATE, OBSCURE_PLANKS);
        sapling(FTZBlocks.OBSCURE_SAPLING);
        signBlock(FTZBlocks.OBSCURE_SIGN.value(), FTZBlocks.OBSCURE_WALL_SIGN.value(), OBSCURE_PLANKS);
        slabBlock(FTZBlocks.OBSCURE_SLAB, "obscure");
        stairsBlock(FTZBlocks.OBSCURE_STAIRS, OBSCURE_PLANKS);
        trapdoorBlock(FTZBlocks.OBSCURE_TRAPDOOR);
        woodBlock(FTZBlocks.OBSCURE_WOOD);
        simpleBlockState(FTZBlocks.POTTED_OBSCURE_SAPLING);
        logBlock(FTZBlocks.STRIPPED_OBSCURE_LOG);
        woodBlock(FTZBlocks.STRIPPED_OBSCURE_WOOD);
        blockWithItem(FTZBlocks.RAW_FANTAZIUM_BLOCK);

        // engineering tables
        engineeringTable(FTZBlocks.OAK_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.SPRUCE_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.BIRCH_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.JUNGLE_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.ACACIA_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.CHERRY_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.DARK_OAK_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.MANGROVE_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.BAMBOO_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.CRIMSON_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.WARPED_ENGINEERING_TABLE);
        engineeringTable(FTZBlocks.OBSCURE_ENGINEERING_TABLE);
    }

    private void fenceGateBlock(DeferredBlock<? extends FenceGateBlock> block, ResourceLocation texture) {
        fenceGateBlock(block.value(), texture);
        blockItem(block);
    }

    private void fenceBlock(DeferredBlock<? extends FenceBlock> block, ResourceLocation texture) {
        fenceBlock(block.value(), texture);
        ResourceLocation id = block.getId();
        itemModels()
                .getBuilder(id.toString())
                .parent(modelFile(id.withPrefix("block/").withSuffix("_inventory")));
    }

    private void createCustomFullFire(DeferredBlock<? extends AncientFlameBlock> block) {
        ResourceLocation base = block.getId().withPrefix("block/");
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block.value());

        builder.part()
                .modelFile(modelFile(base.withSuffix("_floor0")))
                .nextModel()
                .modelFile(modelFile(base.withSuffix("_floor1")))
                .addModel()
                .condition(BlockStateProperties.EAST,false)
                .condition(BlockStateProperties.NORTH,false)
                .condition(BlockStateProperties.SOUTH,false)
                .condition(BlockStateProperties.WEST,false)
                .condition(BlockStateProperties.UP,false).end();

        for (int i = 0; i < 4; i++) {
            int degrees = i * 90;
            BooleanProperty property;
            if (i == 0) property = BlockStateProperties.NORTH;
            else if (i == 1) property = BlockStateProperties.EAST;
            else if (i == 2) property = BlockStateProperties.SOUTH;
            else property = BlockStateProperties.WEST;

            builder.part()
                    .modelFile(modelFile(base.withSuffix("_side0")))
                    .rotationY(degrees)
                    .nextModel()
                    .modelFile(modelFile(base.withSuffix("_side1")))
                    .rotationY(degrees)
                    .nextModel()
                    .modelFile(modelFile(base.withSuffix("_side_alt0")))
                    .rotationY(degrees)
                    .nextModel()
                    .modelFile(modelFile(base.withSuffix("_side_alt1")))
                    .rotationY(degrees)
                    .addModel()
                    .useOr()
                    .nestedGroup()
                    .condition(BlockStateProperties.EAST,false)
                    .condition(BlockStateProperties.NORTH,false)
                    .condition(BlockStateProperties.SOUTH,false)
                    .condition(BlockStateProperties.WEST,false)
                    .condition(BlockStateProperties.UP,false)
                    .end()
                    .nestedGroup()
                    .condition(property, true)
                    .end();
        }

        builder.part()
                .modelFile(modelFile(base.withSuffix("_up0")))
                .nextModel()
                .modelFile(modelFile(base.withSuffix("_up1")))
                .nextModel()
                .modelFile(modelFile(base.withSuffix("_up_alt0")))
                .nextModel()
                .modelFile(modelFile(base.withSuffix("_up_alt1")))
                .addModel()
                .condition(BlockStateProperties.UP, true)
                .end();
    }

    public void logBlock(DeferredBlock<? extends RotatedPillarBlock> block) {
        logBlock(block.value());
        blockItem(block);
    }

    public void woodBlock(DeferredBlock<? extends RotatedPillarBlock> block) {
        getVariantBuilder(block.value()).forAllStates(blockState -> {
            Direction.Axis axis = blockState.getValue(RotatedPillarBlock.AXIS);
            ModelFile file = modelFile(block.getId().withPrefix("block/"));
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(file);
            if (axis == Direction.Axis.X) builder.rotationX(90).rotationY(90);
            else if (axis == Direction.Axis.Z) builder.rotationX(90);
            return builder.build();
        });

        blockItem(block);
    }

    public void trapdoorBlock(@NotNull DeferredBlock<? extends TrapDoorBlock> block) {
        ResourceLocation texture = block.getId().withPrefix("block/");
        String baseName = block.getId().toString();
        ModelFile bottom = this.models().trapdoorBottom(baseName + "_bottom", texture).renderType(RenderType.CUTOUT.name);
        ModelFile top = this.models().trapdoorTop(baseName + "_top", texture).renderType(RenderType.CUTOUT.name);
        ModelFile open = this.models().trapdoorOpen(baseName + "_open", texture).renderType(RenderType.CUTOUT.name);

        this.getVariantBuilder(block.value()).forAllStatesExcept((state) -> {
            int xRot = 0;
            int yRot = (int)(state.getValue(TrapDoorBlock.FACING)).toYRot() + 180;
            boolean isOpen = state.getValue(TrapDoorBlock.OPEN);

            if (!isOpen) {
                yRot = 0;
            }

            yRot %= 360;
            return ConfiguredModel.builder().modelFile(isOpen ? open : (state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)).rotationX(xRot).rotationY(yRot).build();
        }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);

        itemModels().getBuilder(block.getId().toString()).parent(bottom);
    }

    public void stairsBlock(@NotNull DeferredBlock<? extends StairBlock> block, @NotNull ResourceLocation texture) {
        super.stairsBlock(block.value(), texture);
        blockItem(block);
    }

    public void slabBlock(DeferredBlock<? extends SlabBlock> block, String wood) {
        ResourceLocation base = Fantazia.location(wood).withPrefix("block/");
        ModelFile bottom = modelFile(base.withSuffix("_slab"));
        ModelFile full = modelFile(base.withSuffix("_planks"));
        ModelFile top = modelFile(base.withSuffix("_slab_top"));
        slabBlock(block.value(), bottom, top, full);
        blockItem(block);
    }

    public void pressurePlateBlock(@NotNull DeferredBlock<? extends PressurePlateBlock> block, @NotNull ResourceLocation texture) {
        super.pressurePlateBlock(block.get(), texture);
        blockItem(block);
    }

    public void doorBlock(DeferredBlock<? extends DoorBlock> block) {
        ResourceLocation bottom = block.getId().withPrefix("block/").withSuffix("_bottom");
        ResourceLocation top = block.getId().withPrefix("block/").withSuffix("_top");
        doorBlock(block.get(), bottom, top);
    }

    public void simpleBlockItemWithNoBlockModel(DeferredBlock<?> block) {
        simpleBlockState(block);
        blockItem(block);
    }

    public void sapling(DeferredBlock<? extends SaplingBlock> block) {
        simpleBlockState(block);
        itemModels().getBuilder(block.getId().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", block.getId().withPrefix("block/"));
    }

    public void buttonBlock(@NotNull DeferredBlock<? extends ButtonBlock> block, @NotNull ResourceLocation texture) {
        super.buttonBlock(block.get(), texture);
        blockItem(block,"_inventory");
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

    private void simpleBlockState(DeferredBlock<?> block) {
        ResourceLocation id = block.getId();
        ResourceLocation model = id.withPrefix("block/");
        getVariantBuilder(block.get()).partialState().addModels(ConfiguredModel.builder()
                .modelFile(modelFile(model)).build());
    }

    private void engineeringTable(DeferredBlock<? extends EngineeringTableBlock> block) {
        ResourceLocation id = block.getId();
        getVariantBuilder(block.get()).forAllStates(blockState -> {
            ResourceLocation model = id.withPrefix("block/");
            if (blockState.getValue(EngineeringTableBlock.HALF) == DoubleBlockHalf.UPPER)
                model = model.withSuffix("_board");

            Direction direction = blockState.getValue(EngineeringTableBlock.FACING);
            int yRot = switch (direction) {
                case WEST -> 90;
                case NORTH -> 180;
                case EAST -> 270;
                default -> 0;
            };

            return ConfiguredModel.builder()
                    .rotationY(yRot)
                    .modelFile(modelFile(model)).build();
        });
    }

    private ModelFile.ExistingModelFile modelFile(ResourceLocation location) {
        return new ModelFile.ExistingModelFile(location, helper);
    }
}
