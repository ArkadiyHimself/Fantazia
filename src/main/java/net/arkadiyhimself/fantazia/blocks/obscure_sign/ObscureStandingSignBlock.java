package net.arkadiyhimself.fantazia.blocks.obscure_sign;

import net.arkadiyhimself.fantazia.entities.ObscureSignBlockEntity;
import net.arkadiyhimself.fantazia.registries.FTZWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ObscureStandingSignBlock extends StandingSignBlock {

    public ObscureStandingSignBlock(Properties properties) {
        super(FTZWoodTypes.OBSCURE, properties);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ObscureSignBlockEntity(pos, state);
    }
}
