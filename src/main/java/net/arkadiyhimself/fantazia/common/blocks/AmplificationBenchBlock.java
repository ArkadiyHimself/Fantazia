package net.arkadiyhimself.fantazia.common.blocks;

import com.mojang.serialization.MapCodec;
import net.arkadiyhimself.fantazia.common.entity.AmplificationBenchBlockEntity;
import net.arkadiyhimself.fantazia.common.world.inventory.AmplificationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmplificationBenchBlock extends BaseEntityBlock {

    public static final MapCodec<AmplificationBenchBlock> CODEC = simpleCodec(instance -> new AmplificationBenchBlock());
    protected static final VoxelShape SHAPE = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 6.0F, 16.0F);

    public AmplificationBenchBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .lightLevel(p_152692_ -> 3)
                .strength(6.5F, 2400.0F));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new AmplificationBenchBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull  BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof AmplificationBenchBlockEntity entity) {
            Component component = entity.getDisplayName();
            return new SimpleMenuProvider(
                    (containerId, inventory, player) ->
                            new AmplificationMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), component
            );
        } else return null;
    }
}
