package net.arkadiyhimself.fantazia.registries;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.blocks.AncientFlameBlock;
import net.arkadiyhimself.fantazia.blocks.CustomRotatedPillarBlock;
import net.arkadiyhimself.fantazia.blocks.obscure_sign.ObscureHangingSignBlock;
import net.arkadiyhimself.fantazia.blocks.obscure_sign.ObscureStandingSignBlock;
import net.arkadiyhimself.fantazia.blocks.obscure_sign.ObscureWallHangingSignBlock;
import net.arkadiyhimself.fantazia.blocks.obscure_sign.ObscureWallSignBlock;
import net.arkadiyhimself.fantazia.datagen.worldgen.tree.FTZTreeGrowers;
import net.arkadiyhimself.fantazia.events.RegistryEvents;
import net.arkadiyhimself.fantazia.items.RegularBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.compress.utils.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FTZBlocks {

    public static final List<DeferredBlock<?>> WOODEN_BLOCKS = Lists.newArrayList();

    public static final DeferredBlock<AncientFlameBlock> ANCIENT_FLAME;
    public static final DeferredBlock<Block> OBSCURE_PLANKS;
    public static final DeferredBlock<SaplingBlock> OBSCURE_SAPLING;
    public static final DeferredBlock<CustomRotatedPillarBlock> OBSCURE_LOG;
    public static final DeferredBlock<CustomRotatedPillarBlock> OBSCURE_WOOD;
    public static final DeferredBlock<LeavesBlock> OBSCURE_LEAVES;
    public static final DeferredBlock<StairBlock> OBSCURE_STAIRS;
    public static final DeferredBlock<SignBlock> OBSCURE_SIGN;
    public static final DeferredBlock<WallSignBlock> OBSCURE_WALL_SIGN;
    public static final DeferredBlock<CustomRotatedPillarBlock> STRIPPED_OBSCURE_LOG;
    public static final DeferredBlock<CustomRotatedPillarBlock> STRIPPED_OBSCURE_WOOD;
    public static final DeferredBlock<ObscureHangingSignBlock> OBSCURE_HANGING_SIGN;
    public static final DeferredBlock<ObscureWallHangingSignBlock> OBSCURE_WALL_HANGING_SIGN;
    public static final DeferredBlock<PressurePlateBlock> OBSCURE_PRESSURE_PLATE;
    public static final DeferredBlock<TrapDoorBlock> OBSCURE_TRAPDOOR;
    public static final DeferredBlock<FlowerPotBlock> POTTED_OBSCURE_SAPLING;
    public static final DeferredBlock<ButtonBlock> OBSCURE_BUTTON;
    public static final DeferredBlock<SlabBlock> OBSCURE_SLAB;
    public static final DeferredBlock<FenceGateBlock> OBSCURE_FENCE_GATE;
    public static final DeferredBlock<FenceBlock> OBSCURE_FENCE;
    public static final DeferredBlock<DoorBlock> OBSCURE_DOOR;
    public static final DeferredBlock<DropExperienceBlock> FANTAZIUM_ORE;
    public static final DeferredBlock<DropExperienceBlock> DEEPSLATE_FANTAZIUM_ORE;
    public static final DeferredBlock<Block> FANTAZIUM_BLOCK;
    public static final DeferredBlock<Block> RAW_FANTAZIUM_BLOCK;

    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(Fantazia.MODID);
    private static final Map<ResourceLocation, BlockItemSupplier> BLOCK_ITEMS = Maps.newHashMap();

    private static <T extends Block> DeferredBlock<T> registerBlock(final String name, final Supplier<T> blockSupplier, final BlockItemSupplier sup) {
        DeferredBlock<T> block = REGISTER.register(name, blockSupplier);
        if (sup != null) {
            registerItemBlock(name, sup);
            RegistryEvents.BLOCKS.add(block);
        }
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWoodBlock(final String name, final Supplier<T> blockSupplier, final BlockItemSupplier sup) {
        DeferredBlock<T> block = registerBlock(name, blockSupplier, sup);
        WOODEN_BLOCKS.add(block);
        return block;
    }

    private static void registerItemBlock(String name, BlockItemSupplier supplier) {
        BLOCK_ITEMS.put(Fantazia.res(name), supplier);
    }


    protected static Map<ResourceLocation, BlockItemSupplier> getBlockItems() {
        return Collections.unmodifiableMap(BLOCK_ITEMS);
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    public static void onSetup() {
        for (DeferredBlock<?> block : WOODEN_BLOCKS) ((FireBlock) Blocks.FIRE).setFlammable(block.get(), 5, 20);
        ((FireBlock) Blocks.FIRE).setFlammable(OBSCURE_LEAVES.get(), 30, 60);
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    @SuppressWarnings("unused")
    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    @FunctionalInterface
    protected interface BlockItemSupplier extends Function<Block, BlockItem> {
        @Override
        BlockItem apply(Block block);
    }

    static {
        ANCIENT_FLAME = registerBlock("ancient_flame", () -> new AncientFlameBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FIRE).noLootTable()),null);

        // obscure wood
        STRIPPED_OBSCURE_LOG = registerWoodBlock("stripped_obscure_log", () -> new CustomRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(value -> value.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.COLOR_PURPLE : MapColor.TERRACOTTA_PURPLE).instrument(NoteBlockInstrument.BASS).strength(2.5F,5F).sound(SoundType.WOOD).ignitedByLava()), RegularBlockItem::new);
        STRIPPED_OBSCURE_WOOD = registerWoodBlock("stripped_obscure_wood", () -> new CustomRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F,5F).sound(SoundType.WOOD).ignitedByLava()), RegularBlockItem::new);
        OBSCURE_PLANKS = registerWoodBlock("obscure_planks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BASS).strength(2.5F,5F).sound(SoundType.WOOD).ignitedByLava()), RegularBlockItem::new);
        OBSCURE_SAPLING = registerBlock("obscure_sapling", () -> new SaplingBlock(FTZTreeGrowers.OBSCURE, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY)), RegularBlockItem::new);
        OBSCURE_LOG = registerWoodBlock("obscure_log", () -> new CustomRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(value -> value.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.COLOR_PURPLE : MapColor.TERRACOTTA_PURPLE).instrument(NoteBlockInstrument.BASS).strength(2.5F,5F).sound(SoundType.WOOD).ignitedByLava(), STRIPPED_OBSCURE_LOG.value()), RegularBlockItem::new);
        OBSCURE_WOOD = registerWoodBlock("obscure_wood", () -> new CustomRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F,5F).sound(SoundType.WOOD).ignitedByLava(), STRIPPED_OBSCURE_WOOD.value()), RegularBlockItem::new);
        OBSCURE_LEAVES = registerBlock("obscure_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(FTZBlocks::never).isViewBlocking(FTZBlocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(FTZBlocks::never)), RegularBlockItem::new);
        OBSCURE_STAIRS = registerWoodBlock("obscure_stairs", () -> new StairBlock(OBSCURE_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(OBSCURE_PLANKS.get()).strength(2.5F,5F)), RegularBlockItem::new);
        OBSCURE_SIGN = registerBlock("obscure_sign", () -> new ObscureStandingSignBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(2.5F, 5F).ignitedByLava()),null);
        OBSCURE_WALL_SIGN = registerBlock("obscure_wall_sign", () -> new ObscureWallSignBlock(BlockBehaviour.Properties.of().mapColor(OBSCURE_LOG.value().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).dropsLike(OBSCURE_SIGN.value()).ignitedByLava()),null);
        OBSCURE_HANGING_SIGN = registerBlock("obscure_hanging_sign", () -> new ObscureHangingSignBlock(BlockBehaviour.Properties.of().mapColor(OBSCURE_LOG.value().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava()),null);
        OBSCURE_WALL_HANGING_SIGN = registerBlock("obscure_wall_hanging_sign", () -> new ObscureWallHangingSignBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).dropsLike(OBSCURE_SIGN.value()).ignitedByLava()),null);
        OBSCURE_PRESSURE_PLATE = registerBlock("obscure_pressure_plate", () -> new PressurePlateBlock(BlockSetType.DARK_OAK, BlockBehaviour.Properties.of().mapColor(OBSCURE_PLANKS.value().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY)), RegularBlockItem::new);
        OBSCURE_TRAPDOOR = registerBlock("obscure_trapdoor", () -> new TrapDoorBlock(BlockSetType.DARK_OAK, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava()), RegularBlockItem::new);
        POTTED_OBSCURE_SAPLING = registerBlock("potted_obscure_sapling", () -> new FlowerPotBlock(OBSCURE_SAPLING.value(), BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)),null);
        OBSCURE_BUTTON = registerBlock("obscure_button", () -> new ButtonBlock(BlockSetType.DARK_OAK, 30, BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)), RegularBlockItem::new);
        OBSCURE_SLAB = registerWoodBlock("obscure_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.5F, 5F).sound(SoundType.WOOD).ignitedByLava()), RegularBlockItem::new);
        OBSCURE_FENCE_GATE = registerWoodBlock("obscure_fence_gate", () -> new FenceGateBlock(WoodType.DARK_OAK, BlockBehaviour.Properties.of().mapColor(OBSCURE_PLANKS.value().defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.5F, 5.0F).ignitedByLava()), RegularBlockItem::new);
        OBSCURE_FENCE = registerWoodBlock("obscure_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().mapColor(OBSCURE_PLANKS.value().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.5F, 5.0F).ignitedByLava().sound(SoundType.WOOD)), RegularBlockItem::new);
        OBSCURE_DOOR = registerBlock("obscure_door", () -> new DoorBlock(BlockSetType.DARK_OAK, BlockBehaviour.Properties.of().mapColor(OBSCURE_PLANKS.value().defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.5F, 5F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY)), RegularBlockItem::new);

        // fantazium ore
        FANTAZIUM_ORE = registerBlock("fantazium_ore", () -> new DropExperienceBlock(UniformInt.of(6, 9), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.5F,2.5F).lightLevel(blockState -> 7)), RegularBlockItem::new);
        DEEPSLATE_FANTAZIUM_ORE = registerBlock("deepslate_fantazium_ore", () -> new DropExperienceBlock(UniformInt.of(6, 9), BlockBehaviour.Properties.ofFullCopy(FANTAZIUM_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4F,2.5F).sound(SoundType.DEEPSLATE).lightLevel(blockState -> 7)), RegularBlockItem::new);
        FANTAZIUM_BLOCK = registerBlock("fantazium_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.5F,7.0F).sound(SoundType.METAL)), RegularBlockItem::new);
        RAW_FANTAZIUM_BLOCK = registerBlock("raw_fantazium_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.5F,7.0F)), RegularBlockItem::new);
    }
}
