package net.arkadiyhimself.fantazia.common.blocks.obscure_sign;

import net.arkadiyhimself.fantazia.common.entity.ObscureHangingSignBlockEntity;
import net.arkadiyhimself.fantazia.common.registries.FTZWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ObscureHangingSignBlock extends CeilingHangingSignBlock {

    public ObscureHangingSignBlock(Properties properties) {
        super(FTZWoodTypes.OBSCURE, properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ObscureHangingSignBlockEntity(pos, state);
    }
}
