package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataManager;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.DarkFlameTicks;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureManager;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.particless.BloodParticle;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {
    private final Entity entity = (Entity) (Object) this;
    @Inject(at = @At("HEAD"), method = "move")
    private void onMove(MoverType pType, Vec3 pPos, CallbackInfo ci) {
        if (pPos.horizontalDistance() <= 0) return;
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(FTZMobEffects.HAEMORRHAGE) && (pType == MoverType.SELF || pType == MoverType.PLAYER)) {
                float dmg = EffectHelper.bleedingDamage(livingEntity, pPos.subtract(livingEntity.getPosition(1f)));
                boolean flag = livingEntity.hurt(new DamageSource(livingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FTZDamageTypes.BLEEDING)), dmg);
                if (flag) VisualHelper.randomParticleOnModel(livingEntity, BloodParticle.randomBloodParticle(), VisualHelper.ParticleMovement.FALL);
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "displayFireAnimation", cancellable = true)
    private void cancelRenderFire(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            DataManager dataManager = DataGetter.getUnwrap(livingEntity);
            if (dataManager == null) return;
            DarkFlameTicks darkFlameTicks = dataManager.takeData(DarkFlameTicks.class);
            if (darkFlameTicks != null && darkFlameTicks.isBurning()) cir.setReturnValue(false);
        }
    }
    @Inject(at = @At("HEAD"), method = "baseTick", cancellable = true)
    private void tick(CallbackInfo ci) {
        boolean flag = FTZEvents.ForgeExtenstion.onEntityTick(entity);
        if (flag) ci.cancel();
        FeatureGetter.get(entity).ifPresent(FeatureManager::tick);
    }
}
