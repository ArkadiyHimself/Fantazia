package net.arkadiyhimself.fantazia.mixin;

import com.mojang.authlib.GameProfile;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player {

    public MixinServerPlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
            ),
            method = "doCheckFallDamage"
    )
    private int fallingParticles(ServerLevel instance, ParticleOptions options, double x, double y, double z, int amount, double xOffset, double yOffset, double zOffset, double speed) {
        if (!getData(FTZAttachmentTypes.HURT_BY_FALLING_PERK)) return instance.sendParticles(options, x, y, z, amount, xOffset, yOffset, zOffset, speed);
        return 0;
    }
}
