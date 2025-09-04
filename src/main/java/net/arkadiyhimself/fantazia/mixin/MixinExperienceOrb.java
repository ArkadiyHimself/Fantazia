package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperienceOrb extends Entity {

    @Shadow private Player followingPlayer;

    public MixinExperienceOrb(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void accelerate(CallbackInfo ci) {
        if (followingPlayer == null) return;
        if (FantazicUtil.holdsDataComponent(followingPlayer, FTZDataComponentTypes.WISDOM_TRANSFER.value())) {
            Vec3 delta = getDeltaMovement();
            Vec3 vec3 = new Vec3(this.followingPlayer.getX() - this.getX(), this.followingPlayer.getY() + (double) this.followingPlayer.getEyeHeight() / 2.0 - this.getY(), this.followingPlayer.getZ() - this.getZ()).normalize();
            setDeltaMovement(delta.add(vec3.normalize().scale(0.25)));
        }
    }
}
