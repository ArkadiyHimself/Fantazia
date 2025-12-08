package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.data.tags.FTZBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ItemUsedOnLocationTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"), method = "place")
    private void onBlockPlaced(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (player == null || !player.getData(FTZAttachmentTypes.FERTILIZATION_PERK)) return;
        fantazia$applyBoneMeal(player.level(), context.getClickedPos());
    }

    @Unique
    private static void fantazia$applyBoneMeal(Level level, BlockPos blockPos) {
        BlockState blockstate = level.getBlockState(blockPos);
        Block block = blockstate.getBlock();
        if (blockstate.is(FTZBlockTags.IGNORED_BY_FERTILIZATION_PERK)) return;
        if (block instanceof BonemealableBlock bonemealableBlock && bonemealableBlock.isValidBonemealTarget(level, blockPos, blockstate) && level instanceof ServerLevel serverLevel) {
            level.levelEvent(1505, blockPos, 15);
            for (int i = 0; i < 2; i++)
                if (bonemealableBlock.isBonemealSuccess(serverLevel, serverLevel.random, blockPos, blockstate))
                    bonemealableBlock.performBonemeal(serverLevel, serverLevel.random, blockPos, blockstate);
        }
    }
}
