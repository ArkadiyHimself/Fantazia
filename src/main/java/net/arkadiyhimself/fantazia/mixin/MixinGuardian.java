package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Guardian.class)
public abstract class MixinGuardian extends Monster {

    protected MixinGuardian(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), method = "hurt")
    private boolean cancelDamage(LivingEntity instance, DamageSource ev, float amount) {
        if (getData(FTZAttachmentTypes.GUARDIAN_NO_THORNS)) {
            setData(FTZAttachmentTypes.GUARDIAN_NO_THORNS, false);
            return false;
        } else return instance.hurt(ev, amount);
    }
}