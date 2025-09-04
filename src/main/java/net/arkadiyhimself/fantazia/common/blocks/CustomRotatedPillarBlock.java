package net.arkadiyhimself.fantazia.common.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomRotatedPillarBlock extends RotatedPillarBlock {

    private final @Nullable Block stripped;

    public CustomRotatedPillarBlock(Properties properties, @Nullable Block stripped) {
        super(properties);
        this.stripped = stripped;
    }

    public CustomRotatedPillarBlock(Properties properties) {
        this(properties, null);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@NotNull BlockState state, @NotNull UseOnContext context, @NotNull ItemAbility itemAbility, boolean simulate) {
        ItemStack stack = context.getItemInHand();
        if (stripped == null || !stack.canPerformAction(itemAbility) || itemAbility != ItemAbilities.AXE_STRIP) return super.getToolModifiedState(state, context, itemAbility, simulate);
        return stripped.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
    }
}
