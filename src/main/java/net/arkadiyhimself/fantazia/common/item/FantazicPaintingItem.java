package net.arkadiyhimself.fantazia.common.item;

import net.arkadiyhimself.fantazia.common.entity.FantazicPainting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class FantazicPaintingItem extends HangingEntityItem {

    public FantazicPaintingItem() {
        super(EntityType.PAINTING, new Properties());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());

        if (context.getPlayer() == null || !this.mayPlace(context.getPlayer(), context.getClickedFace(), context.getItemInHand(), blockPos)) {
            return InteractionResult.FAIL;
        } else {
            FantazicPainting paintingEntity = new FantazicPainting(context.getLevel(), blockPos, context.getClickedFace());

            if (paintingEntity.survives()) {
                if (!context.getLevel().isClientSide()) {
                    paintingEntity.playPlacementSound();
                    context.getLevel().addFreshEntity(paintingEntity);
                }

                 context.getItemInHand().shrink(1);
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            } else return InteractionResult.CONSUME;
        }
    }
}
