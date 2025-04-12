package net.arkadiyhimself.fantazia.blocks.obscure_sign;

import net.arkadiyhimself.fantazia.entities.ObscureSignBlockEntity;
import net.arkadiyhimself.fantazia.registries.FTZWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ObscureWallSignBlock extends WallSignBlock {

    public ObscureWallSignBlock(Properties properties) {
        super(FTZWoodTypes.OBSCURE, properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ObscureSignBlockEntity(pos, state);
    }
}
