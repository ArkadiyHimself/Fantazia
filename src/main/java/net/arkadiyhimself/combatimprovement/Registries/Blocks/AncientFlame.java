package net.arkadiyhimself.combatimprovement.Registries.Blocks;

import net.arkadiyhimself.combatimprovement.Registries.CombatSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class AncientFlame extends Block {
    private final float damage;
    public AncientFlame() {
        super(BlockBehaviour.Properties.of(Material.FIRE, MaterialColor.COLOR_BLACK).noCollission().instabreak().lightLevel((light) -> 10).sound(SoundType.WOOL));
        damage = 3;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return this.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        pEntity.hurt(CombatSources.ANCIENT_FLAME, this.damage);
    }
}
