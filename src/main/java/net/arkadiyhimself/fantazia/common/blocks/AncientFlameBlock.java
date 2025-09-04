package net.arkadiyhimself.fantazia.common.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AncientFlameBlock extends BaseFireBlock {
    public static final MapCodec<AncientFlameBlock> CODEC = simpleCodec(AncientFlameBlock::new);
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_53467_) -> p_53467_.getKey() != Direction.DOWN).collect(Util.toMap());
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private final float DAMAGE;
    private final Map<BlockState, VoxelShape> shapesCache;

    public AncientFlameBlock(Properties properties) {
        super(properties, 3f);
        this.DAMAGE = 3f;
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(AGE, 0));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), AncientFlameBlock::calculateShape)));
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);

        boolean flag1 = level.getBiome(pos).is(BiomeTags.INCREASED_FIRE_BURNOUT);
        int k = flag1 ? -50 : 0;

        int i = state.getValue(AGE);
        state.setValue(AGE, i + 1);

        this.checkBurnOut(level, pos.east(),300 + k, random, i, Direction.WEST);
        this.checkBurnOut(level, pos.west(),300 + k, random, i, Direction.EAST);
        this.checkBurnOut(level, pos.below(),250 + k, random, i, Direction.UP);
        this.checkBurnOut(level, pos.above(),250 + k, random, i, Direction.DOWN);
        this.checkBurnOut(level, pos.north(),300 + k, random, i, Direction.SOUTH);
        this.checkBurnOut(level, pos.south(),300 + k, random, i, Direction.NORTH);
    }

    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {
        if (pEntity instanceof LivingEntity livingEntity) {
            float dmg = this.DAMAGE;
            if (livingEntity instanceof SnowGolem) dmg *= 2.5f;
            DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(pLevel);
            if (sources != null) pEntity.hurt(sources.ancientFlame(), dmg);
            int flameTicks = livingEntity instanceof Player player && (player.getAbilities().invulnerable) ? 2 : 120;
            livingEntity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).set(flameTicks);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, AGE);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        return true;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseFireBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.getStateForPlacement(pContext.getLevel(), pContext.getClickedPos());
    }

    @Override
    protected boolean canBurn(@NotNull BlockState pState) {
        return true;
    }
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return this.shapesCache.get(pState);
    }
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockPos pNeighborPos) {
        return getStateForPlacement(pLevel, pPos);
    }

    private static VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelshape = Shapes.empty();
        if (blockState.getValue(UP)) voxelshape = Shapes.or(voxelshape, UP_AABB);
        if (blockState.getValue(NORTH)) voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        if (blockState.getValue(SOUTH)) voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        if (blockState.getValue(EAST)) voxelshape = Shapes.or(voxelshape, EAST_AABB);
        if (blockState.getValue(WEST)) voxelshape = Shapes.or(voxelshape, WEST_AABB);
        return voxelshape.isEmpty() ? DOWN_AABB : voxelshape;
    }

    public BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        if (!canCatchFire(pLevel, pPos.relative(Direction.DOWN)) && !blockstate.isFaceSturdy(pLevel, blockpos, Direction.UP)) {
            BlockState blockstate1 = this.defaultBlockState();
            for(Direction direction : Direction.values()) {
                BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(direction);
                if (booleanproperty != null) blockstate1 = blockstate1.setValue(booleanproperty, canCatchFire(pLevel, pPos.relative(direction)));
            }
            return blockstate1;
        } else return this.defaultBlockState();
    }

    public boolean canCatchFire(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return !state.isAir() && !state.is(Blocks.FIRE) && !state.is(FTZBlocks.ANCIENT_FLAME.get());
    }

    private void checkBurnOut(Level level, BlockPos pos, int chance, RandomSource random, int age, Direction face) {
        int i = level.getBlockState(pos).getFlammability(level, pos, face);
        if (random.nextInt(chance) < i) {
            BlockState blockstate = level.getBlockState(pos);
            blockstate.onCaughtFire(level, pos, face, null);
            if (random.nextInt(age + 10) > 5) level.removeBlock(pos, false);
        }
    }
}
