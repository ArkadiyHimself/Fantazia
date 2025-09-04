package net.arkadiyhimself.fantazia.common.item.expendable;

import net.arkadiyhimself.fantazia.common.blocks.AncientFlameBlock;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class AncientSparkItem extends ExpendableItem {
    public AncientSparkItem() {
        super(Rarity.UNCOMMON);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockPos actual = blockpos.relative(pContext.getClickedFace());
        BlockState blockstate = level.getBlockState(blockpos);
        boolean flag = false;
        if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate) && AncientFlameBlock.canBePlacedAt(level, actual, pContext.getClickedFace())) {

            this.playSound(level, actual);

            level.setBlockAndUpdate(actual, FTZBlocks.ANCIENT_FLAME.get().getStateForPlacement(level, actual));
            level.gameEvent(pContext.getPlayer(), GameEvent.BLOCK_PLACE, actual);
            flag = true;
        }

        if (flag) {
            pContext.getItemInHand().shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else return InteractionResult.FAIL;
    }
    private void playSound(Level pLevel, BlockPos pPos) {
        RandomSource randomsource = pLevel.getRandom();
        pLevel.playSound(null, pPos, FTZSoundEvents.ANCIENT_SPARK.get(), SoundSource.BLOCKS, 0.5F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F);
    }
}
