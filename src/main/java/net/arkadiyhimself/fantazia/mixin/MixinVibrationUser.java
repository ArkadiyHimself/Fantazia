package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.monster.warden.Warden$VibrationUser")
public abstract class MixinVibrationUser implements VibrationSystem.User {
    @Shadow @Final
    Warden this$0;
    boolean isDiggingOrEmerging() {
        return this.this$0.hasPose(Pose.DIGGING) || this.this$0.hasPose(Pose.EMERGING);
    }
    @Override
    public boolean canReceiveVibration(@NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull GameEvent pGameEvent, GameEvent.@NotNull Context pContext) {
        if (this.this$0.hasEffect(FTZMobEffects.DEAFENED)) return false;
        if (!this.this$0.isNoAi() && !this.this$0.isDeadOrDying() && !this.this$0.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && !isDiggingOrEmerging() && pLevel.getWorldBorder().isWithinBounds(pPos)) {
            Entity entity = pContext.sourceEntity();
            if (entity instanceof LivingEntity livingentity) {
                if (!this.this$0.canTargetEntity(livingentity)) return false;
            }

            return true;
        } else {
            return false;
        }
    }
}
