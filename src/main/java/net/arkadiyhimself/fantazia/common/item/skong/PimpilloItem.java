package net.arkadiyhimself.fantazia.common.item.skong;

import net.arkadiyhimself.fantazia.common.entity.skong.Pimpillo;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.ToolCapacityLevelFunction;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.ToolDamageLevelFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PimpilloItem extends RechargeableToolItem {

    public PimpilloItem() {
        super(new ChatFormatting[]{ChatFormatting.RED}, 20);
    }

    @Override
    protected void successfulUse(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand, float damage) {
        if (!level.isClientSide()) {
            Pimpillo pimpillo = new Pimpillo(level, player, damage);
            pimpillo.setPos(player.getEyePosition().add(0, -0.1, 0));
            pimpillo.shootFrom(player, 0.435f);
            level.addFreshEntity(pimpillo);
            level.playSound(null, player.blockPosition(), FTZSoundEvents.PIMPILLO_THROW.value(), SoundSource.NEUTRAL);
        }
    }

    @Override
    public RechargeableToolData defaultData() {
        return RechargeableToolData.builder()
                .ingredient(Items.GUNPOWDER, 1)
                .ingredient(Items.LEATHER, 1)
                .ingredient(Items.STRING, 2)
                .capacity(ToolCapacityLevelFunction.baseAndPerLevel(8, 2))
                .damage(ToolDamageLevelFunction.baseAndPerLevel(1.5f, 0.25f))
                .initialAmount(5).build();
    }
}
