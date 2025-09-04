package net.arkadiyhimself.fantazia.common.entity;

import net.arkadiyhimself.fantazia.common.registries.FTZBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ObscureHangingSignBlockEntity extends SignBlockEntity {

    public ObscureHangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FTZBlockEntityTypes.OBSCURE_HANGING_SIGN.get(), blockPos, blockState);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return FTZBlockEntityTypes.OBSCURE_HANGING_SIGN.get();
    }
}
