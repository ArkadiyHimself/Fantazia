package net.arkadiyhimself.fantazia.common.entity;

import net.arkadiyhimself.fantazia.common.registries.FTZBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ObscureSignBlockEntity extends SignBlockEntity {

    public ObscureSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(FTZBlockEntityTypes.OBSCURE_SIGN.get(), pos, blockState);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return FTZBlockEntityTypes.OBSCURE_SIGN.get();
    }
}
