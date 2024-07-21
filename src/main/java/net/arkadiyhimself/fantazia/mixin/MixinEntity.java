package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.events.custom.NewEvents;
import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.arkadiyhimself.fantazia.registry.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.arkadiyhimself.fantazia.registry.ParticleRegistry;
import net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData.CommonData;
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

import java.util.Random;

@Mixin(Entity.class)
public class MixinEntity {
    private Entity entity = (Entity) (Object) this;
    @Inject(at = @At("HEAD"), method = "move")
    private void onMove(MoverType pType, Vec3 pPos, CallbackInfo ci) {
        if (pPos.horizontalDistance() <= 0) return;
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(MobEffectRegistry.HAEMORRHAGE.get()) && (pType == MoverType.SELF || pType == MoverType.PLAYER)) {
                float dmg = WhereMagicHappens.Abilities.calculateBleedingDMG(livingEntity, pPos.subtract(livingEntity.getPosition(1f)));
                boolean flag = livingEntity.hurt(new DamageSource(livingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.BLEEDING)), dmg);
                if (flag) {
                    int num = new Random().nextInt(0, ParticleRegistry.BLOOD.size());
                    WhereMagicHappens.Abilities.randomParticleOnModel(livingEntity, ParticleRegistry.BLOOD.get(num).get(), WhereMagicHappens.Abilities.ParticleMovement.FALL);
                }
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "displayFireAnimation", cancellable = true)
    private void cancelRenderFire(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity) {
            CommonData data = AttachCommonData.getUnwrap(livingEntity);
            if (data != null && data.isAncientBurning()) cir.setReturnValue(false);
        }
    }
    @Inject(at = @At("HEAD"), method = "baseTick", cancellable = true)
    private void tick(CallbackInfo ci) {
        boolean flag = NewEvents.ForgeExtenstion.onEntityTick(entity);
        if (flag) ci.cancel();
    }
}
