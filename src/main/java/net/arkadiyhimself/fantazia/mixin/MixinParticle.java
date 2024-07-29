package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.events.custom.VanillaEventsExtension;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public class MixinParticle {
    @Shadow protected double x;

    @Shadow protected double y;

    @Shadow protected double z;

    @Shadow protected double xd;

    @Shadow protected double yd;

    @Shadow protected double zd;

    @Shadow protected int age;

    @Shadow protected float rCol;

    @Shadow protected float gCol;

    @Shadow protected float bCol;

    @Shadow protected boolean hasPhysics;

    @Shadow protected boolean onGround;

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tick(CallbackInfo ci) {
        Particle particle = (Particle) (Object) this;
        VanillaEventsExtension.ParticleTickEvent event = FTZEvents.ForgeExtenstion.onParticleTick(particle, new Vec3(x, y, z), new Vec3(xd, yd, zd), rCol, gCol, bCol, age, hasPhysics, onGround);
        if (event.isCanceled()) {
            ci.cancel();
            return;
        }
        age = event.age;
        hasPhysics = event.hasPhysics;
        onGround = event.onGround;
    }
}
