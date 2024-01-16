package net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.checkerframework.checker.units.qual.C;

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
    @Cancelable
    public static class MobAttackEvent extends LivingEvent {
        private final Entity target;
        public MobAttackEvent(Mob entity, Entity target) {
            super(entity);
            this.target = target;
        }
        public Entity getTarget()
        {
            return target;
        }
    }
    @Cancelable
    public static class DeathPreventationEvent extends LivingEvent {
        public DeathPreventationEvent(LivingEntity entity, Object cause) {
            super(entity);
            this.cause = cause;
        }
        // a mob effect or an item which causes the death preventation; just use «cause instance of MobEffect» or smth like that
        private final Object cause;
        public Object getCause() { return cause; }
    }
}
