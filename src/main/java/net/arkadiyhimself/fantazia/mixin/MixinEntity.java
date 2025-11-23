package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.client.ClientEvents;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.entity.DashStone;
import net.arkadiyhimself.fantazia.common.entity.skong.Pimpillo;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract Vec3 getDeltaMovement();

    @Shadow private Vec3 deltaMovement;

    @Shadow public abstract void setDeltaMovement(double x, double y, double z);

    @Unique
    private final Entity fantazia$entity = (Entity) (Object) this;

    @Inject(at = @At("HEAD"), method = "move")
    private void onMove(MoverType pType, Vec3 pPos, CallbackInfo ci) {
        if (pPos.horizontalDistance() <= 0) return;
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(FTZMobEffects.HAEMORRHAGE) && (pType == MoverType.SELF || pType == MoverType.PLAYER)) {
                float dmg = LivingEffectHelper.bleedingDamage(livingEntity, pPos);
                LevelAttributesHelper.hurtEntity(livingEntity, dmg, DamageSourcesHolder::bleeding);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "displayFireAnimation", cancellable = true)
    private void cancelRenderFire(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() > 0) cir.setReturnValue(false);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"), method = "move")
    private void collideWithWall(Entity instance, double x, double y, double z) {
        if (!(instance instanceof Pimpillo)) setDeltaMovement(x, y, z);
        boolean xColl = x == 0;
        boolean zColl = z == 0;
        Vec3 delta = getDeltaMovement();
        double dx = delta.x;
        if (xColl) dx *= -0.8;
        double dz = delta.z;
        if (zColl) dz *= -0.8;
        deltaMovement = new Vec3(dx, y, dz);
    }

    @Inject(at = @At("HEAD"), method = "getTeamColor", cancellable = true)
    private void teamColor(CallbackInfoReturnable<Integer> cir) {
        DashHolder dashHolder = PlayerAbilityHelper.takeHolder(Minecraft.getInstance().player, DashHolder.class);
        if (dashHolder != null) {
            DashStone dashStone = dashHolder.getDashstoneEntity(Minecraft.getInstance().level);
            if (dashStone != null && dashStone.isProtectorClient(fantazia$entity)) cir.setReturnValue(2286);
        }
        if (fantazia$entity instanceof Arrow arrow && arrow.getData(FTZAttachmentTypes.HAS_FURY))
            cir.setReturnValue(12586510);

        if (!(fantazia$entity instanceof LivingEntity livingEntity)) return;
        if (LivingEffectHelper.hasEffect(livingEntity, FTZMobEffects.FURY.value())) cir.setReturnValue(12586510);
        if (livingEntity == ClientEvents.suitableTarget && Screen.hasShiftDown()) cir.setReturnValue(13788415);
    }

    @Inject(at = @At("HEAD"), method = "getGravity", cancellable = true)
    private void noGravity(CallbackInfoReturnable<Double> cir) {
        if (fantazia$entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing()) cir.setReturnValue(0.0);
        }
    }
}
