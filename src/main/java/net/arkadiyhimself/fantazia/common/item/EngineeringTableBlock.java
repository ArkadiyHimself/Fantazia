package net.arkadiyhimself.fantazia.common.item;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.entity.EngineeringTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EngineeringTableBlock extends BaseEntityBlock {

    public static final MapCodec<EngineeringTableBlock> CODEC = simpleCodec(instance -> new EngineeringTableBlock());

    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static final DirectionProperty FACING;

    private static final VoxelShape LEG1;
    private static final VoxelShape LEG2;
    private static final VoxelShape LEG3;
    private static final VoxelShape LEG4;
    private static final VoxelShape DESKTOP;
    private static final VoxelShape BASE;

    private static final VoxelShape BOARD_NORTH;
    private static final VoxelShape BOARD_EAST;
    private static final VoxelShape BOARD_SOUTH;
    private static final VoxelShape BOARD_WEST;

    public EngineeringTableBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .strength(3.5F, 4.5F)
                .sound(SoundType.WOOD)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult
    ) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof EngineeringTableBlockEntity table && table.isValid()) {
            boolean flag = table.returnBlueprint(player);
            if (flag) table.setChanged();
            return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            preventDropFromBottomPart(level, pos, state, player);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        } else {
            return facingState.getBlock() instanceof EngineeringTableBlock && facingState.getValue(HALF) != doubleblockhalf
                    ? facingState.setValue(HALF, doubleblockhalf)
                    : Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return BASE;
        else return switch (state.getValue(FACING)) {
            case EAST -> BOARD_EAST;
            case SOUTH -> BOARD_SOUTH;
            case WEST -> BOARD_WEST;
            default -> BOARD_NORTH;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        if (!context.getLevel().getBlockState(pos.above()).isAir()) return null;
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new EngineeringTableBlockEntity(blockPos, blockState);
    }

    protected static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockState1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    public static void dropContentsOnDestroy(BlockState state, BlockState newState, Level level, BlockPos pos) {
        if (state.is(newState.getBlock())) return;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EngineeringTableBlockEntity entity) {
            entity.onDestroy();
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }

    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

        LEG1 = Block.box(0,0,0,4,10,4);
        LEG2 = Block.box(12,0,0,16,10,4);
        LEG3 = Block.box(0,0,12,4,10,16);
        LEG4 = Block.box(12,0,12,16,10,16);
        DESKTOP = Block.box(0D, 10D, 0D, 16D, 14D, 16D);
        BASE = Shapes.or(DESKTOP, LEG1, LEG2, LEG3, LEG4);

        BOARD_NORTH = Block.box(1D,-2D,10D,15D,14D,16D);
        BOARD_EAST = Block.box(0D,-2D,1D,6D,14D,15D);
        BOARD_SOUTH = Block.box(1D,-2D,0D,15D,14D,6D);
        BOARD_WEST = Block.box(10D,-2D,1D,16D,14D,15D);
    }
}
