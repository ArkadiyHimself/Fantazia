package net.arkadiyhimself.fantazia.blocks;

import com.google.common.collect.ImmutableMap;
import net.arkadiyhimself.fantazia.advanced.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.data.DataManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.data.newdata.DarkFlameTicks;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AncientFlameBlock extends BaseFireBlock {
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_53467_) -> p_53467_.getKey() != Direction.DOWN).collect(Util.toMap());
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private final float DAMAGE;
    private final Map<BlockState, VoxelShape> shapesCache;
    public AncientFlameBlock() {
        super(Properties.copy(Blocks.FIRE), 3f);
        this.DAMAGE = 3f;
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), AncientFlameBlock::calculateShape)));
    }
    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {
        if (pEntity instanceof LivingEntity livingEntity) {
            float dmg = this.DAMAGE;
            if (livingEntity instanceof SnowGolem) dmg *= 2.5f;
            pEntity.hurt(new DamageSource(pLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FTZDamageTypes.ANCIENT_FLAME)), dmg);
            DataManager dataManager = DataGetter.getUnwrap(livingEntity);
            if (dataManager == null) return;
            dataManager.getData(DarkFlameTicks.class).ifPresent(darkFlameTicks -> darkFlameTicks.setFlameTicks(livingEntity instanceof Player player && (player.isCreative() || player.isSpectator()) ? 3 : 100));
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
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
    public BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockPos pNeighborPos) {
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
    protected BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
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
        return !world.getBlockState(pos).isAir();
    }
}
