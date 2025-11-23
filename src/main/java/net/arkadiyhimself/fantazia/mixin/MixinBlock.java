package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.entity.skong.Pimpillo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(at = @At("HEAD"), method = "updateEntityAfterFallOn", cancellable = true)
    private void cancel(BlockGetter level, Entity entity, CallbackInfo ci) {
        if (entity instanceof Pimpillo) ci.cancel();
    }
}
