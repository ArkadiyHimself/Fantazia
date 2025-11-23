package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.data.tags.FTZMobEffectTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Arrow.class)
public abstract class MixinArrow extends AbstractArrow {

    @Shadow protected abstract PotionContents getPotionContents();

    protected MixinArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;getColor()I"), method = "updateColor")
    private int setColor(PotionContents instance) {
        List<MobEffectInstance> instances = Lists.newArrayList();
        for (MobEffectInstance mobEffectInstance : instance.getAllEffects()) {
            if (!mobEffectInstance.getEffect().is(FTZMobEffectTags.NO_PARTICLES_ON_ARROWS)) instances.add(mobEffectInstance);
        }
        return instances.isEmpty() ? -1 : PotionContents.getColor(instances);
    }

    @Inject(at = @At(value = "HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
        if (true) return;
        if (level() instanceof ServerLevel serverLevel) {
            boolean snow = false;
            for (MobEffectInstance instance : getPotionContents().getAllEffects())
                if (instance.is(FTZMobEffects.FROZEN)) snow = true;

            if (snow) {
                Vec3 pos = position();
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 2, 0,0,0, 0);
            }
        }
    }
}
