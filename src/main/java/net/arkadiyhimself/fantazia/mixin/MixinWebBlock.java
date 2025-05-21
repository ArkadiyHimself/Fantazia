package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WebBlock.class)
public class MixinWebBlock {

    @Inject(at = @At("HEAD"), method = "entityInside", cancellable = true)
    private void cancelStuck(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity.getData(FTZAttachmentTypes.WALL_CLIMBING_COBWEB)) ci.cancel();
    }
}
