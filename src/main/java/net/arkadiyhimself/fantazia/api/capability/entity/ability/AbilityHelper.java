package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.StaminaData;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.VibrationListen;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AbilityHelper {
    public static Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float)Math.PI / 180F);
        float f1 = -pYRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }
    public static Vec3 dashDeltaMovement(Vec3 lookAngle, double speed, boolean hor) {
        Vec3 velocity = lookAngle.normalize().scale(speed);
        if (velocity.y() == 0 && hor) velocity = velocity.add(0,0.001,0);
        return velocity;
    }
    public static Vec3 dashDeltaMovement(LivingEntity entity, double velocity, boolean horizontal) {
        return dashDeltaMovement(calculateViewVector(horizontal ? 0 : entity.getXRot(), entity.getYRot()), velocity, horizontal);
    }
    public static boolean doubleJump(Player player) {
        StaminaData staminaData = AbilityGetter.takeAbilityHolder(player, StaminaData.class);
        if (staminaData != null && !staminaData.wasteStamina(1.75f, true)) return false;

        if (!FTZEvents.onDoubleJump(player)) return false;
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS);
        Vec3 vec3 = player.getDeltaMovement();
        player.setDeltaMovement(vec3.x, 0.64 + player.getJumpBoostPower(), vec3.z);
        player.fallDistance = -2f;
        player.hurtMarked = true;
        return true;
    }
    public static boolean accelerateFlying(Player player) {
        StaminaData staminaData = AbilityGetter.takeAbilityHolder(player, StaminaData.class);
        if (staminaData != null && !staminaData.wasteStamina(3f, true)) return false;

        if (!FTZEvents.onDoubleJump(player)) return false;
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS);

        Vec3 vec31 = player.getLookAngle();
        Vec3 vec32 = player.getDeltaMovement();

        player.setDeltaMovement(vec32.add(vec31.x * 0.1D + (vec31.x * 2.5D - vec32.x) * 0.5D, vec31.y * 0.1D + (vec31.y * 2.5D - vec32.y) * 0.5D, vec31.z * 0.1D + (vec31.z * 2.5D - vec32.z) * 0.5D));
        player.hurtMarked = true;
        return true;
    }
    public static boolean facesAttack(LivingEntity blocker, DamageSource source) {
        Vec3 vec32 = source.getSourcePosition();
        if (vec32 == null) return false;
        Vec3 vec3 = blocker.getViewVector(1.0F);
        Vec3 vec31 = vec32.vectorTo(blocker.position()).normalize();
        vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
        return vec31.dot(vec3) < 0.0D;
    }
    public static boolean shouldListen(ServerLevel pLevel, BlockPos pPos, GameEvent.Context pContext, LivingEntity entity) {
        if (pContext.sourceEntity() != null && pContext.sourceEntity().isCrouching() || pContext.sourceEntity() == entity) return false;
        return !entity.isDeadOrDying() && pLevel.getWorldBorder().isWithinBounds(pPos) && !entity.hasEffect(FTZMobEffects.DEAFENED.get());
    }

    public static boolean isOccluded(Level pLevel, Vec3 pFrom, Vec3 pTo) {
        Vec3 vec3 = new Vec3((double) Mth.floor(pFrom.x) + 0.5D, (double) Mth.floor(pFrom.y) + 0.5D, (double) Mth.floor(pFrom.z) + 0.5D);
        Vec3 vec31 = new Vec3((double) Mth.floor(pTo.x) + 0.5D, (double) Mth.floor(pTo.y) + 0.5D, (double) Mth.floor(pTo.z) + 0.5D);

        for (Direction direction : Direction.values()) {
            Vec3 vec32 = vec3.relative(direction, 1.0E-5F);
            if (pLevel.isBlockInLine(new ClipBlockStateContext(vec32, vec31, (p_223780_) -> p_223780_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() != HitResult.Type.BLOCK) return false;
        }
        return true;
    }
    public static void tryListen(ServerLevel pLevel, GameEvent.Context pContext, Vec3 pPos, ServerPlayer player) {
        if (pContext.sourceEntity() == null || !(pContext.sourceEntity() instanceof LivingEntity livingEntity)) return;
        Vec3 vec3 = player.getPosition(1f);
        if (AbilityHelper.shouldListen(pLevel, BlockPos.containing(pPos), pContext, player) && !AbilityHelper.isOccluded(pLevel, pPos, vec3)) {
            VibrationListen vibrationListen = AbilityGetter.takeAbilityHolder(player, VibrationListen.class);
            if (vibrationListen == null || !vibrationListen.listen()) return;
            vibrationListen.madeSound(livingEntity);
        }
    }
    public static TalentsHolder.ProgressHolder getProgressHolder(Player player) {
        TalentsHolder talentsHolder = AbilityGetter.takeAbilityHolder(player, TalentsHolder.class);
        return talentsHolder == null ? null : talentsHolder.getProgressHolder();
    }
}
