package net.arkadiyhimself.fantazia.common.item.skong;

import net.arkadiyhimself.fantazia.common.entity.BlockFly;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.ToolCapacityLevelFunction;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.ToolDamageLevelFunction;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlockFlyItem extends RechargeableToolItem {

    public BlockFlyItem() {
        super(new ChatFormatting[]{ChatFormatting.BLUE}, 60);
    }

    @Override
    protected void successfulUse(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand, float damage) {
        if (!level.isClientSide()) {
            BlockFly blockFly = new BlockFly(level, player, damage);
            Vec3 delta = RandomUtil.randomVec3().normalize().scale(0.75);
            blockFly.setPos(player.position().add(0, 0.6, 0));
            blockFly.setDeltaMovement(delta);
            blockFly.setYRot(-((float) Mth.atan2(delta.x, delta.z)) * (180.0F / (float)Math.PI));
            blockFly.yBodyRot = blockFly.getYRot();
            level.addFreshEntity(blockFly);
            blockFly.playSound(FTZSoundEvents.BLOCK_FLY_BUZZING.value());
            level.playSound(null, player.blockPosition(), FTZSoundEvents.BLOCK_FLY_WINDUP.value(), SoundSource.NEUTRAL);
        }
    }

    @Override
    public RechargeableToolData defaultData() {
        return RechargeableToolData.builder()
                .ingredient(Items.REDSTONE, 1)
                .ingredient(Items.SOUL_TORCH, 1)
                .ingredient(Items.COPPER_INGOT, 2)
                .capacity(ToolCapacityLevelFunction.baseAndPerLevel(10, 3))
                .damage(ToolDamageLevelFunction.baseAndPerLevel(3.5f, 0.75f))
                .initialAmount(7).build();
    }
}
