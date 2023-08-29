package net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class VanillaEventsExtension {
    @Cancelable
    public static class ParticleTickEvent extends Event {
        public final Vec3 position;
        public final Vec3 deltaMovement;
        public final Particle particleType;
        public final float red;
        public final float green;
        public final float blue;
        public int age;
        public boolean hasPhysics;
        public boolean onGround;
        public ParticleTickEvent(Particle particle, Vec3 position, Vec3 deltaMovement, float red, float green, float blue, int age, boolean hasPhysics, boolean onGround) {
            this.particleType = particle;
            this.position = position;
            this.deltaMovement = deltaMovement;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.age = age;
            this.hasPhysics = hasPhysics;
            this.onGround = onGround;
        }

    }
}
