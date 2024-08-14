package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.StaminaData;
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
    public static Vec3 dashVelocity(Vec3 lookAngle, double speed, boolean hor) {
        Vec3 velocity = lookAngle.normalize().scale(speed);
        if (velocity.y() == 0 && hor) velocity = velocity.add(0,0.01,0);
        return velocity;
    }
    public static Vec3 dashVelocity(LivingEntity entity, double velocity, boolean horizontal) {
        return dashVelocity(calculateViewVector(horizontal ? 0 : entity.getXRot(), entity.getYRot()), velocity, horizontal);
    }
    public static void doubleJump(ServerPlayer serverPlayer) {
        AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
        if (abilityManager == null) return;
        StaminaData staminaData = abilityManager.takeAbility(StaminaData.class);
        if (staminaData == null) return;
        boolean doJump = staminaData.wasteStamina(1.75f, true);
        if (!doJump) return;

        boolean event = FTZEvents.onDoubleJump(serverPlayer);
        if (event) {
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS);
            Vec3 vec3 = serverPlayer.getDeltaMovement();
            serverPlayer.setDeltaMovement(vec3.x, 0.64 + serverPlayer.getJumpBoostPower(), vec3.z);
            serverPlayer.fallDistance = -2f;
            serverPlayer.hurtMarked = true;
        }
    }
    public static boolean facesAttack(LivingEntity blocker, DamageSource source) {
        Vec3 vec32 = source.getSourcePosition();
        if (vec32 != null) {
            Vec3 vec3 = blocker.getViewVector(1.0F);
            Vec3 vec31 = vec32.vectorTo(blocker.position()).normalize();
            vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
            return vec31.dot(vec3) < 0.0D;
        } else return false;
    }
    @SuppressWarnings("ConstantConditions")
    public static boolean shouldListen(ServerLevel pLevel, BlockPos pPos, GameEvent.Context pContext, LivingEntity entity) {
        if (pContext.sourceEntity() != null && pContext.sourceEntity().isCrouching() || pContext.sourceEntity() == entity) return false;
        return !entity.isDeadOrDying() && pLevel.getWorldBorder().isWithinBounds(pPos) && !entity.hasEffect(FTZMobEffects.DEAFENED);
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
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager == null) return;
            VibrationListen vibrationListen = abilityManager.takeAbility(VibrationListen.class);
            if (vibrationListen == null || !vibrationListen.listen()) return;
            vibrationListen.madeSound(livingEntity);
        }
    }
}
