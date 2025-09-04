package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFlagsPredicate.class)
public class MixinEntityFlagsPredicate {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isOnFire()Z"), method = "matches")
    private boolean isOnFire(Entity instance) {
        return instance.isOnFire() || instance.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() > 0;
    }
}
