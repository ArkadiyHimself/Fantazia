package net.arkadiyhimself.fantazia.common.item.skong;

import net.arkadiyhimself.fantazia.common.entity.skong.ThrownPin;
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

public class ThrowingPinItem extends RechargeableToolItem {

    public ThrowingPinItem() {
        super(new ChatFormatting[]{ChatFormatting.BLUE}, 5);
    }

    @Override
    protected void successfulUse(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand, float damage) {
        if (!level.isClientSide()) {
            ThrownPin thrownPin = new ThrownPin(level, player, damage);
            thrownPin.setPos(player.getEyePosition().add(0, -0.1, 0));
            thrownPin.shootFrom(player, 1.25f);
            thrownPin.rotate();
            level.addFreshEntity(thrownPin);
            level.playSound(null, player.blockPosition(), FTZSoundEvents.THROWING_PIN_THROW.value(), SoundSource.NEUTRAL);
        }
    }

    @Override
    public RechargeableToolData defaultData() {
        return RechargeableToolData.builder()
                .ingredient(Items.IRON_NUGGET, 3)
                .ingredient(Items.STICK, 1)
                .capacity(ToolCapacityLevelFunction.baseAndPerLevel(16, 4))
                .damage(ToolDamageLevelFunction.baseAndPerLevel(4f, 0.75f))
                .initialAmount(10).build();
    }
}
