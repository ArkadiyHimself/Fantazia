package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.api.IMixinShulkerBullet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ShulkerBullet.class)
public class MixinShulkerBullet implements IMixinShulkerBullet {

    @Shadow @Nullable private net.minecraft.world.entity.Entity finalTarget;

    @Shadow @Nullable private UUID targetId;

    @Override
    public void setTarget(Entity entity) {
        this.finalTarget = entity;
        this.targetId = entity.getUUID();
    }
}
